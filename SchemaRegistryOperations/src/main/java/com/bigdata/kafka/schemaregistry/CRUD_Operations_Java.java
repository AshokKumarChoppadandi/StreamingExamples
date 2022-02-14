package com.bigdata.kafka.schemaregistry;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.apache.avro.Schema;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

public class CRUD_Operations_Java {
    public CachedSchemaRegistryClient client = null;
    Schema.Parser parser = new Schema.Parser();

    public CRUD_Operations_Java(String SCHEMA_REGISTRY_URL, int identityMapCapacity) {
        client = new CachedSchemaRegistryClient(SCHEMA_REGISTRY_URL, identityMapCapacity);
    }

    public static void main(String[] args) {
        /*String SCHEMA_REGISTRY_URL = "http://192.168.0.101:8081";
        String SUBJECT_NAME = "Test-2-value";
        String inputSchemaFilePath = "./src/main/resources/Test1.avsc";
        String inputSchemaFilePathNew = "./src/main/resources/Test2.avsc";
        String compatibilityMode = "FULL_TRANSITIVE";
        String operation = "delete_subject";
        String versionNumber = "5";*/

        String SCHEMA_REGISTRY_URL = args[0];
        String SUBJECT_NAME = args[1];
        String inputSchemaFilePath = args[2];
        String inputSchemaFilePathNew = args[3];
        String compatibilityMode = args[4];
        String operation = args[5];
        String versionNumber = args[6];

        CRUD_Operations_Java ops = new CRUD_Operations_Java(SCHEMA_REGISTRY_URL, 100);

        switch (operation) {
            case "create":
                // Create a New Subject
                ops.registerSubject(SUBJECT_NAME, ops.generateAvroSchemaFromFile(inputSchemaFilePath));
                System.out.printf("SUBJECT with name %s created successfully...!!!\n", SUBJECT_NAME);
                break;
            case "get_id":
                // Get the schema id for a given Subject
                int schemaId = ops.getSubjectSchemaId(SUBJECT_NAME);
                System.out.printf("The schema ID for the given subject %s is - %d\n", SUBJECT_NAME, schemaId);
            case "update_compatibility":
                // Update Subject compatibility
                ops.updateSubjectCompatibility(SUBJECT_NAME, compatibilityMode);
                System.out.printf("SUBJECT %s compatibility mode is set to - %s\n", SUBJECT_NAME, compatibilityMode);
                break;
            case "get_compatibility":
                // Get Subject compatibility
                String subjectCompatibility = ops.getSubjectCompatibility(SUBJECT_NAME);
                System.out.printf("Compatibility for subject %s is - %s\n", SUBJECT_NAME, subjectCompatibility);
                break;
            case "update_schema":
                // Update a Subject with new schema
                ops.registerSubject(SUBJECT_NAME, ops.generateAvroSchemaFromFile(inputSchemaFilePathNew));
                System.out.printf("SUBJECT with name %s updated successfully...!!!\n", SUBJECT_NAME);
                break;
            case "latest_schema":
                // Get the latest schema of the Subject
                String schemaString = ops.getLatestSchema(SUBJECT_NAME);
                System.out.printf("Latest Schema of Subject %s is -\n%s", SUBJECT_NAME, schemaString);
                break;
            case "latest_version":
                // Get the latest schema of the Subject
                int latestVersion = ops.getLatestSchemaVersion(SUBJECT_NAME);
                System.out.printf("Latest version of Subject %s is - %d\n", SUBJECT_NAME, latestVersion);
                break;
            case "version_schema":
                // Get Schema for the given version of a Subject
                SchemaMetadata schemaMetadata = ops.getSchemaForSubjectVersion(SUBJECT_NAME, Integer.parseInt(versionNumber));
                System.out.println(SUBJECT_NAME + " - Version : " + schemaMetadata.getVersion() + ", Schema : " + schemaMetadata.getSchema());
                break;
            case "all_versions_schema":
                // Get schema for all versions of the Subject
                ops.getAllVersionsForSubject(SUBJECT_NAME);
                break;
            case "all_subjects":
                // Get schema for all the Subjects and versions
                ops.getAllSubjects();
                break;
            case "delete_version":
                // Delete Schema for a given version
                ops.deleteSchemaVersion(SUBJECT_NAME, versionNumber);
                System.out.println(SUBJECT_NAME + " - Version : " + versionNumber + " is deleted...!!!");
                break;
            case "delete_subject":
                // Delete subject
                ops.deleteSubject(SUBJECT_NAME);
                System.out.println(SUBJECT_NAME + " deleted successfully!");
                break;
            default:
                System.out.println("Not a proper input!!!");
                break;
        }
    }

    private int getSubjectSchemaId(String subjectName) {

        try {
            SchemaMetadata schemaMetadata = client.getLatestSchemaMetadata(subjectName);
            return schemaMetadata.getId();
        } catch (IOException | RestClientException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void getAllSubjects() {
        try {
            Collection<String> subjects = client.getAllSubjects();
            subjects.forEach(subject -> {
                try {
                    List<Integer> versions = client.getAllVersions(subject);
                    versions.forEach(version -> {
                        try {
                            SchemaMetadata schemaMetadata = client.getSchemaMetadata(subject, version);
                            System.out.println(subject + " - Version : " + schemaMetadata.getVersion() + ", Schema : " + schemaMetadata.getSchema());
                        } catch (IOException | RestClientException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (IOException | RestClientException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException | RestClientException e) {
            e.printStackTrace();
        }
    }

    public int getLatestSchemaVersion(String subjectName) {
        try {
            SchemaMetadata schemaMetadata = client.getLatestSchemaMetadata(subjectName);
            return schemaMetadata.getVersion();
        } catch (IOException | RestClientException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getLatestSchema(String subjectName) {
        try {
            SchemaMetadata schemaMetadata = client.getLatestSchemaMetadata(subjectName);
            return schemaMetadata.getSchema();
        } catch (IOException | RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SchemaMetadata getSchemaForSubjectVersion(String subjectName, int version) {
        try {
            return client.getSchemaMetadata(subjectName, version);
        } catch (IOException | RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getAllVersionsForSubject(String subjectName) {
        try {
            List<Integer> versionsList = client.getAllVersions(subjectName);
            versionsList.forEach(version -> {
                try {
                    SchemaMetadata schemaMetadata = client.getSchemaMetadata(subjectName, version);
                    System.out.println(subjectName + " : " + schemaMetadata.getVersion() + " : " + schemaMetadata.getSchema());
                } catch (IOException | RestClientException e) {
                    e.printStackTrace();
                }

            });

        } catch (IOException | RestClientException e) {
            e.printStackTrace();
        }
    }

    public void deleteSubject(String subjectName) {
        try {
            client.deleteSubject(subjectName);
        } catch (IOException | RestClientException e) {
            e.printStackTrace();
        }
    }

    public void deleteSchemaVersion(String subjectName, String version) {
        try {
            client.deleteSchemaVersion(subjectName, version);
        } catch (IOException | RestClientException e) {
            e.printStackTrace();
        }
    }

    public Schema generateAvroSchemaFromString(String schemaString) {
        return parser.parse(schemaString);
    }

    public Schema generateAvroSchemaFromFile(String inputFilePath) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(inputFilePath);
            String schemaString = IOUtils.toString(inputStream, Charset.defaultCharset());
            return parser.parse(schemaString);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert inputStream != null;
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void updateSubjectCompatibility(String subjectName, String compatibility) {
        try {
            client.updateCompatibility(subjectName, compatibility);
            System.out.printf("Topic compatibility is successfully updated to %s...!!!\n", compatibility);
        } catch (IOException | RestClientException e) {
            e.printStackTrace();
        }
    }

    public String getSubjectCompatibility(String subjectName) {
        try {
            return client.getCompatibility(subjectName);
        } catch (IOException | RestClientException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void registerSubject(String subjectName, Schema schema) {
        try {
            client.register(subjectName, schema);
            System.out.printf("Topic Schema / Subject - %s created successfully...!!!\n", subjectName);
        } catch (IOException | RestClientException e) {
            e.printStackTrace();
        }
    }


}
