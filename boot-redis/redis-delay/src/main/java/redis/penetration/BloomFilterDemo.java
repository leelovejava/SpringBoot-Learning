package com.leelovejava.redis.penetration;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import org.springframework.util.StringUtils;

/**
 * bloomFilter过滤器
 * @author leelovejava
 * @date 2019/11/7 23:02
 **/
public class BloomFilterDemo {
    private final BloomFilter<String> dealIdBloomFilter = BloomFilter.create(new Funnel<String>() {

        private static final long serialVersionUID = 1L;

        @Override
        public void funnel(String arg0, PrimitiveSink arg1) {

            arg1.putString(arg0, Charsets.UTF_8);
        }

    }, 1024*1024*32);

    public synchronized boolean containsDealId(String deal_id){

        if(StringUtils.isEmpty(deal_id)){
            System.out.println("deal_id is null");
            return true;
        }

        boolean exists = dealIdBloomFilter.mightContain(deal_id);
        if(!exists){
            dealIdBloomFilter.put(deal_id);
        }
        return exists;
    }
}
