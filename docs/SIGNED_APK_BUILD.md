# Building a signed APK on demand

The repository can build a **signed release APK** on demand using the
[`Build signed APK`](../.github/workflows/signed-apk.yml) GitHub Actions workflow.
It is triggered manually (`workflow_dispatch`) and never runs automatically, so
signing credentials are never exposed to pull-request builds.

The Gradle build is already set up for signing: `app/build.gradle.kts` enables the
`release` signing config whenever the `RELEASE_STORE_FILE`, `RELEASE_STORE_PASSWORD`,
`RELEASE_KEY_ALIAS`, and `RELEASE_KEY_PASSWORD` project properties are present. The
workflow supplies these from repository secrets.

## 1. Create a keystore (if you don't have one)

```bash
keytool -genkey -v \
  -keystore release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias thumbkey
```

Keep `release.jks` and its passwords safe and out of version control.

## 2. Add the repository secrets

Go to **Settings → Secrets and variables → Actions → New repository secret** and add:

| Secret                    | Value                                                        |
| ------------------------- | ----------------------------------------------------------- |
| `RELEASE_KEYSTORE_BASE64` | base64-encoded keystore (see below)                         |
| `RELEASE_STORE_PASSWORD`  | the keystore password                                       |
| `RELEASE_KEY_ALIAS`       | the key alias (e.g. `thumbkey`)                             |
| `RELEASE_KEY_PASSWORD`    | the key password                                            |

Encode the keystore as a single line of base64:

```bash
# Linux
base64 -w0 release.jks

# macOS
base64 release.jks | tr -d '\n'
```

Paste the output as the value of `RELEASE_KEYSTORE_BASE64`.

## 3. Run the build

1. Open the **Actions** tab.
2. Select **Build signed APK** in the left sidebar.
3. Click **Run workflow**, choose the branch, and confirm.

## 4. Download the result

When the run finishes, open it and download the **`thumbkey-release-apk`** artifact
from the **Artifacts** section. It contains the signed `app-release.apk`. The workflow
also runs `apksigner verify --print-certs` and prints the signing certificate in the
build log.
