package com.bloodnetwork.app.util;

import android.content.Context;

import com.bloodnetwork.app.model.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Appends newly registered users to a writable CSV in internal storage.
 * The file path is: context.getFilesDir()/users_registered.csv
 * This complements the seeded assets/users.csv which is read-only.
 */
public class UserCsvWriter {

    private static final String FILE_NAME = "users_registered.csv";
    private static final String HEADER = "UserID,Username,Password,Role,LinkedID,Approved";

    public static void append(Context context, User user) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        try {
            boolean isNew = !file.exists();
            try (FileWriter fw = new FileWriter(file, true)) {
                if (isNew) fw.write(HEADER + "\n");
                fw.write(user.getId() + ","
                        + user.getUsername() + ","
                        + user.getPassword() + ","
                        + user.getRole().name() + ","
                        + user.getLinkedId() + ","
                        + user.isApproved() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Rewrites the entire file (used when approval status changes). */
    public static void rewrite(Context context, java.util.List<User> users) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        try (FileWriter fw = new FileWriter(file, false)) {
            fw.write(HEADER + "\n");
            for (User u : users) {
                fw.write(u.getId() + ","
                        + u.getUsername() + ","
                        + u.getPassword() + ","
                        + u.getRole().name() + ","
                        + u.getLinkedId() + ","
                        + u.isApproved() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
