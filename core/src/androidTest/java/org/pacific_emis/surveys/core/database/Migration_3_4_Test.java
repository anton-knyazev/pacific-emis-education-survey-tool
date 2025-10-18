package org.pacific_emis.surveys.core.database;

import static org.pacific_emis.surveys.core.data.persistence.SchoolInfoDatabase.MIGRATION_3_4;

import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.pacific_emis.surveys.core.data.persistence.SchoolInfoDatabase;

import java.io.IOException;

/**
 * Created by Xena Bolshakova on 12.01.2024.
 * Copyright (c) 2024 Omega https://omega-r.com
 */

@LargeTest
public class Migration_3_4_Test {
    private static final String TEST_DB = "schools.database";

    @Rule
    public MigrationTestHelper helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
            SchoolInfoDatabase.class.getCanonicalName(),
            new FrameworkSQLiteOpenHelperFactory());

    @Test
    public void migrateAll() throws IOException {
        // Create earliest version of the database.
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 3);
        db.execSQL("INSERT OR ABORT INTO `RoomSurveyLog` (`id`,`surveyType`,`createUser`,`schoolName`,`schoolId`,`surveyTag`,`logAction`,`appRegion`)" +
                " VALUES (1, \"SCHOOL_ACCREDITATION\",\"test@omega-r.com\",\"Awak\",\"PN301\",\"2024-01-18\",\"DELETED\",\"FSM\")");

        db.close();

        // Open latest version of the database. Room will validate the schema
        // once all migrations execute.
        db = helper.runMigrationsAndValidate(TEST_DB, 4, false, MIGRATION_3_4);
        db.close();
    }
}