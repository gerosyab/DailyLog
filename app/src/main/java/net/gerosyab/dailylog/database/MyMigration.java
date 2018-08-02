package net.gerosyab.dailylog.database;

import net.gerosyab.dailylog.data.Category;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class MyMigration implements RealmMigration {
    long newOrder = 0;
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        //set order column with ascending value in Category Entity
        if(oldVersion == 1){
            RealmObjectSchema realmObjectSchema = schema.get(Category.class.getSimpleName());
            if (realmObjectSchema != null) {
                realmObjectSchema.transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.setLong("order", newOrder++);
                            }
                        });
            }
            oldVersion++;
        }
    }
}
