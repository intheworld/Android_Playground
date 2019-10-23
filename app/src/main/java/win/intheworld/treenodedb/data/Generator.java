package win.intheworld.treenodedb.data;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.PropertyType;
import org.greenrobot.greendao.generator.Schema;


/**
 * Created by swliu on 18-12-28.
 * liushuwei@xiaomi.com
 */

public class Generator {

    private static void oldGenerate() {
        Schema schema = new Schema(1, "com.xiaomi.voiceassistant.AiSettings.db");
        schema.enableKeepSectionsByDefault();

        Entity nodeEntity = schema.addEntity("NodeEntity");
        nodeEntity.addIdProperty();
        nodeEntity.addProperty(PropertyType.String, "name");
        Property parentIdProperty = nodeEntity.addLongProperty("parentId").getProperty();
        nodeEntity.addToOne(nodeEntity, parentIdProperty).setName("parent");
        nodeEntity.addToMany(nodeEntity, parentIdProperty).setName("children");


        Entity plateEntity = schema.addEntity("PlateEntity");
        plateEntity.addIdProperty();
        plateEntity.addProperty(PropertyType.String, "name");
        Property nodeIdProperty = plateEntity.addLongProperty("nodeId").getProperty();
        plateEntity.addToOne(nodeEntity, nodeIdProperty);

        try {
            DaoGenerator daoGenerator = new DaoGenerator();
            daoGenerator.generateAll(schema, "./app/src/main/java/");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
