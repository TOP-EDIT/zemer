package me.knighthat.utils

object Repository {

    const val GITHUB = "https://github.com"
    const val GITHUB_API = "https://api.github.com"

    // Zemer's own public source code (GPL-3.0)
    const val OWNER = "TOP-EDIT"
    const val REPO = "$OWNER/zemer"
    const val REPO_URL = "$GITHUB/$REPO"

    // The original project Zemer is based on
    const val UPSTREAM_URL = "$GITHUB/knighthat/Kreate"

    const val LATEST_TAG_URL = "$REPO/releases/latest"

    const val ISSUE_TEMPLATE_PATH = "/issues"
    const val FEATURE_REQUEST_TEMPLATE_PATH = "/issues"
}
