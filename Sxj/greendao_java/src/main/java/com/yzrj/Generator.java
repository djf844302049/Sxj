package com.yzrj;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class Generator {
    public static void main(String[] args) throws Exception {

        int version = 4;
        String defaultPackage = "test.greenDAO.bean";
        Schema schema = new Schema(version, defaultPackage);
        schema.setDefaultJavaPackageDao("test.greenDAO.dao");

        //添加实体
        addEntity(schema);
        String outDir = "../Sxj/sxj/src/main/java-gen";
        new DaoGenerator().generateAll(schema, outDir);

    }

    private static void addEntity(Schema schema) {
        Entity entity = schema.addEntity("Duty");

        entity.setTableName("things");

        entity.implementsSerializable();

        entity.addIdProperty().autoincrement();
        entity.addStringProperty("title").notNull();
        entity.addStringProperty("info");
        entity.addStringProperty("type");
        entity.addBooleanProperty("status");
        entity.addDateProperty("date");

        entity.addLongProperty("clocktime");
        entity.addIntProperty("clockfanshi");
        entity.addIntProperty("clocktqtime");

        entity.addStringProperty("city");
        entity.addStringProperty("weather");
    }
}