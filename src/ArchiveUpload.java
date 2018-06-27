/**
 * MIT License
 * Copyright (c) 2018 Baldeep Hira
 * Contact @ https://bhira.net/
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.text.NumberFormat;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.UploadResult;


/**
 * ArchiveUpload is used to upload large files (archives) to AWS Glacier. For large
 * files it automatically splits the file into multiple chunks and uses multi-part
 * API to upload the file in pieces and then then stitch it together.
 */
public class ArchiveUpload {

    private static final String logFile = "archiveUpload.log";
    private static final String propsFile = "archive.properties";
    private static final String propEndpoint = "awsEndpoint";
    private static final String propVaultName = "vaultName";
    private static final String propUploadFiles = "uploadFiles";

    /**
     * Upload specified archives to AWS. It uses multi-part API from AWS to chunk the file
     * and load the parts to AWS container.  This implementation uploads archives only to
     * AWS Glacier.
     * <p>
     * The AWS client configuration is done using "config" and "credentials" files stored
     * under ~/.aws folder.  The config file contains region and output properties.
     * The config file contains aws_access_key_id and aws_secret_access_key properties.
     * <p>
     * Configure archive.properties to set awsEndpoint, glacier vault name and comma
     * separated names of files to be uploaded.
     *
     * @throws IOException Thrown if there is an issue reading input or writing to AWS.
     */
    public static void main(String[] args) throws IOException {

        // Initialize the log file handle
        BufferedWriter writer = null;
        try {
            // Open the log file in append mode for writing
            writer = new BufferedWriter(new FileWriter(logFile, true));

            // Load properties from archive.properties
            Properties props = new Properties();
            FileInputStream propStream = null;
            try {
                propStream = new FileInputStream(propsFile);
                props.load(propStream);
            } catch (Exception ex) {
                throw new Exception("Could not load properties from " + propsFile);
            } finally {
                if (propStream != null) {
                    propStream.close();
                }
            }

            // init variables
            String endpoint = props.getProperty(propEndpoint);
            String vaultName = props.getProperty(propVaultName);
            String files = props.getProperty(propUploadFiles);
            String[] fileList = cslToArray(files);
            writer.write("\n\nStarting archive upload...");
            writer.write("\n  awsEndpoint:   " + endpoint);
            writer.write("\n  vaultName:     " + vaultName);
            writer.write("\n  uploadFiles:   " + files);
            writer.write("\n");

            // validate properties read from archive.properties
            if (endpoint == null || endpoint.trim().isEmpty()) {
                throw new Exception("awsEndpoint property in archive.properties is invalid.");
            }
            if (vaultName == null || vaultName.trim().isEmpty()) {
                throw new Exception("vaultName property in archive.properties is invalid.");
            }
            if (files == null || files.trim().isEmpty()) {
                throw new Exception("uploadFiles property in archive.properties is invalid.");
            }

            // Configure Amazon Glacier Client
            ProfileCredentialsProvider credentials = new ProfileCredentialsProvider();
            AmazonGlacierClient client = new AmazonGlacierClient(credentials);
            client.setEndpoint(endpoint);
            ArchiveTransferManager atm = new ArchiveTransferManager(client, credentials);

            // Init number formatters
            NumberFormat nf = NumberFormat.getInstance();

            // Iterate through all filenames and upload them
            for (String file : fileList) {
                File inputFile = new File(file);
                Date startTime = new Date();
                writer.write("\n  Archive:       " + inputFile);
                writer.write("\n  File Size:     " + nf.format(inputFile.length()));
                writer.write("\n  Start Time:    " + startTime);
                UploadResult result = atm.upload(vaultName, "upload on " + startTime, inputFile);
                Date endTime = new Date();
                writer.write("\n  End Time:      " + endTime);
                writer.write("\n  Duration:      " + formatTimeDiff(startTime, endTime));
                writer.write("\n  Archive ID:    " + result.getArchiveId());
                writer.write("\n");
            }

        } catch (Throwable t) {
            if (writer != null) {
                writer.write("\nError: " + t.getMessage());
                writer.write("\n");
            } else {
                System.err.println(t);
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * Calculate the time difference between start and end date and format
     * it into human readable format.
     *
     * @param start the Date instance representing start time.
     * @param end the Date instance representing end time.
     * @return the time interval formatted in human readable format.
     */
    static String formatTimeDiff(Date start, Date end) {
        long diff = end.getTime() - start.getTime();
        diff /= 1000;
        long secs = diff % 60;
        long mins = (diff / 60) % 60;
        long hours = diff / (60 * 60);
        StringBuilder b = new StringBuilder();
        b.append(hours).append("hr ");
        b.append(mins).append("min ");
        b.append(secs).append("s");
        return b.toString();
    }


    /**
     * Split the given string on comma into array of strings.  Trim leading
     * and trailing whitespaces around items in the comma separated list.
     *
     * @param str the string to be split on commas
     * @return an array of strings
     */
    static String[] cslToArray(String str) {
        if (str == null || str.trim().isEmpty()) {
          return null;
        }
        return str.trim().split("\\s*,\\s*");
    }
}