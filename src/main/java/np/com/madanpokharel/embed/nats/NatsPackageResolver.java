/*
 * Copyright 2022 Madan Pokharel
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package np.com.madanpokharel.embed.nats;

import de.flapdoodle.embed.process.config.store.DistributionPackage;
import de.flapdoodle.embed.process.config.store.FileSet;
import de.flapdoodle.embed.process.config.store.FileType;
import de.flapdoodle.embed.process.config.store.PackageResolver;
import de.flapdoodle.embed.process.distribution.ArchiveType;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.os.BitSize;
import de.flapdoodle.os.Platform;

final class NatsPackageResolver implements PackageResolver {
    private final ServerType serverType;

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
    public String getPath(Distribution distribution) {
        String version = distribution.version().asInDownloadPath();
        String arch = getArch(distribution.platform().architecture().bitSize());
        String platform = getPlatform(distribution.platform());
        return version + "/" + serverType.getServerName() + "-" + version + '-' + platform + '-' + arch + ".zip";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributionPackage packageFor(Distribution distribution) {
        return DistributionPackage.builder().archiveType(ArchiveType.ZIP)
            .fileSet(getExecutableFileSet(distribution))
            .archivePath(getPath(distribution))
            .build();
    }

    private String getPlatform(Platform platform) {
        switch (platform.operatingSystem()) {
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
        switch (distribution.platform().operatingSystem()) {
            case Linux:
            case OS_X:
                break;
            case Windows:
                executableName += ".exe";
                break;
            default:
                throw new IllegalArgumentException(
                        "Unknown platform " + distribution.platform());
        }
        return FileSet.builder()
                .addEntry(FileType.Executable, executableName)
                .build();
    }
}
