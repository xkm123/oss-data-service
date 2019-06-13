package com.berry.ossdataservice.service.impl;

import com.berry.ossdataservice.api.WriteShardResponse;
import com.berry.ossdataservice.common.util.NetworkUtils;
import com.berry.ossdataservice.config.GlobalProperties;
import com.berry.ossdataservice.service.IShardSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Title ShardSaveServiceImpl
 * Description
 * Copyright (c) 2019
 * Company  上海思贤信息技术股份有限公司
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/10 18:29
 */
@Service
public class ShardSaveServiceImpl implements IShardSaveService {

    @Autowired
    private GlobalProperties globalProperties;

    @Override
    public WriteShardResponse writeShard(String username, String bucketName, String fileName, Integer shardIndex, byte[] data) throws IOException {
        String userBucketPath = username + "/" + bucketName;
        File file = new File(globalProperties.getDataPath(), userBucketPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath = file.getPath() + "/" + fileName + "." + shardIndex;
        FileOutputStream outputStream = new FileOutputStream(filePath);
        outputStream.write(data);
        outputStream.close();
        WriteShardResponse response = new WriteShardResponse()
                .setIp(NetworkUtils.getIpAddress())
                .setPath(filePath);
        return response;
    }

    @Override
    public byte[] readShard(String path) throws IOException {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            FileInputStream in = new FileInputStream(file);
            int size = in.available();
            byte[] result = new byte[size];
            int read = in.read(result);
            if (read != size) {
                throw new RuntimeException("数据不完整");
            }
            in.close();
            return result;
        }
        return null;
    }
}