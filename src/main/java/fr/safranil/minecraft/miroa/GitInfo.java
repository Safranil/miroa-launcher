package fr.safranil.minecraft.miroa;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Safranil on 01/07/2016.
 */
final class GitInfo {
    static private GitInfo instance;
    static private String NOPE = java.lang.management.ManagementFactory.getRuntimeMXBean().
            getInputArguments().toString().indexOf("-agentlib:jdwp") > 0 ? "DEBUG" : "N/A";
    private String branch = NOPE;
    private String commitId = NOPE;
    private String describe = NOPE;
    private String commitUserName = NOPE;
    private String commitMessageFull = NOPE;
    private String commitTime = NOPE;
    private String closestTagName = NOPE;
    private String closestTagCommitCount = NOPE;
    private String buildUserName = NOPE;
    private String buildTime = NOPE;
    private String buildVersion = NOPE;

    private GitInfo() throws IOException {
        Properties properties = new Properties();

        InputStream is = getClass().getResourceAsStream("git.properties");
        if (is != null) {
            properties.load(is);

            branch = String.valueOf(properties.get("git.branch"));

            commitId = String.valueOf(properties.get("git.commit.id"));
            describe = String.valueOf(properties.get("git.commit.id.describe"));
            commitUserName = String.valueOf(properties.get("git.commit.user.name"));
            commitMessageFull = String.valueOf(properties.get("git.commit.message.full"));
            commitTime = String.valueOf(properties.get("git.commit.time"));
            closestTagName = String.valueOf(properties.get("git.closest.tag.name"));
            closestTagCommitCount = String.valueOf(properties.get("git.closest.tag.commit.count"));

            buildUserName = String.valueOf(properties.get("git.build.user.name"));
            buildTime = String.valueOf(properties.get("git.build.time"));
            buildVersion = String.valueOf(properties.get("git.build.version"));
        }
    }

    static GitInfo getInstance()
    {
        if (instance == null)
        {
            try {
                instance = new GitInfo();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }


    String getDescribe() {
        return describe;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(650);

        sb.append("Describe       : ");
        sb.append(describe);
        sb.append("\n\nBuild User     : ");
        sb.append(buildUserName);
        sb.append("\nBuild Version  : ");
        sb.append(buildVersion);
        sb.append("\nBuild Time     : ");
        sb.append(buildTime);
        sb.append("\n\nCommit Id      : ");
        sb.append(commitId);
        sb.append("\nCommit Time    : ");
        sb.append(commitTime);
        sb.append("\nCommit User    : ");
        sb.append(commitUserName);
        sb.append("\nCommit Message : ");
        sb.append(commitMessageFull);
        sb.append("\nBranch         : ");
        sb.append(branch);
        sb.append("\nClosest Tag    : ");
        sb.append(closestTagName);
        sb.append(" (distance : ");
        sb.append(closestTagCommitCount);
        sb.append(")");

        return sb.toString();
    }
}
