package com.leelovejava.service;


import com.leelovejava.domain.MetaDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MetaDatabaseServiceTest {

    @Autowired
    private MetaDatabaseService metaDatabaseService;


    @Test
    public void testSave(){
        MetaDatabase metaDatabase = new MetaDatabase();
        metaDatabase.setName("default");
        metaDatabase.setLocation("hdfs://hadoop000:8020/user/hive/warehouse");

        metaDatabaseService.save(metaDatabase);
    }

    @Test
    public void testQuery(){
        Iterable<MetaDatabase> metaDatabases = metaDatabaseService.query();
        for(MetaDatabase metaDatabase: metaDatabases) {
            System.out.println(metaDatabase.getId());
            System.out.println(metaDatabase.getName());
            System.out.println(metaDatabase.getLocation());
        }
    }

}
