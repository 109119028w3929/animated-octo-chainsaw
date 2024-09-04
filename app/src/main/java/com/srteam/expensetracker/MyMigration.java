//package com.srteam.expensetracker;
//
//import io.realm.RealmMigration;
//
//public class MyMigration implements RealmMigration {
//    @Override
//    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
//        RealmSchema schema = realm.getSchema();
//
//        if (oldVersion == 0) {
//            schema.get("Person")
//                    .addField("age", int.class);
//            oldVersion++;
//        }
//    }
//}
