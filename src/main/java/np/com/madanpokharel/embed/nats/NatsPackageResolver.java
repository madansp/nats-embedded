package np.com.madanpokharel.embed.nats;

import de.flapdoodle.embed.process.config.store.FileSet;
import de.flapdoodle.embed.process.config.store.FileType;
import de.flapdoodle.embed.process.config.store.IPackageResolver;
import de.flapdoodle.embed.process.distribution.ArchiveType;
import de.flapdoodle.embed.process.distribution.BitSize;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.Platform;

final class NatsPackageResolver implements IPackageResolver {
    private ServerType serverType;

    /**
     * <p>Constructor for NatsPackageResolver.</p>
     *
     * @param serverType a {@link np.com.madanpokharel.embed.nats.ServerType} object.
     */
    NatsPackageResolver(ServerType serverType) {
        this.serverType = serverType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileSet getFileSet(Distribution distribution) {
        return getExecutableFileSet(distribution);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ArchiveType getArchiveType(Distribution distribution) {
        return ArchiveType.ZIP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPath(Distribution distribution) {
        String version = distribution.getVersion().asInDownloadPath();
        String arch = getArch(distribution.getBitsize());
        String platform = getPlatform(distribution.getPlatform());
        return version + "/" + serverType.getServerName() + "-" + version + '-' + platform + '-' + arch + ".zip";
    }

    private String getPlatform(Platform platform) {
        switch (platform) {
            case OS_X:
                return "darwin";
            case Linux:
                return "linux";
            case Windows:
                return "windows";
            default:
                throw new IllegalArgumentException("Unknown platform " + platform);
        }
    }

    private String getArch(BitSize bitSize) {
        String arch;
        if (System.getProperty("os.arch").startsWith("arm")) {
            switch (bitSize) {
                case B32:
                    arch = "arm7";
                    break;
                case B64:
                    arch = "arm64";
                    break;
                default:
                    arch = "arm6";
            }
        } else {
            switch (bitSize) {
                case B32:
                    arch = "386";
                    break;
                case B64:
                    arch = "amd64";
                    break;
                default:
                    throw new IllegalArgumentException("Unknown bit size " + bitSize);
            }
        }

        return arch;
    }

    private FileSet getExecutableFileSet(Distribution distribution) {
        String executableName = serverType.getServerName();
        switch (distribution.getPlatform()) {
            case Linux:
            case OS_X:
                break;
            case Windows:
                executableName += ".exe";
                break;
            default:
                throw new IllegalArgumentException(
                        "Unknown platform " + distribution.getPlatform());
        }
        return FileSet.builder()
                .addEntry(FileType.Executable, executableName)
                .build();
    }
}
