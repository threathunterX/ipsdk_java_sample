package com.yazx.demo;

import com.google.gson.Gson;
import com.yazx.model.ScoreConf;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.*;


/**
 * @author yazx
 * @desc 获取ip 打分配置服务
 */
public class GetIpScoreService {

    private String mSnUser;
    private String mSnKey;

    public GetIpScoreService(String snUser, String snKey) {
        mSnUser = snUser;
        mSnKey = snKey;
    }


    private byte[] encryptAES(String phoneno) {
        try {
            SecretKeySpec key = new SecretKeySpec(mSnKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
            byte[] phonenobyteContent = phoneno.getBytes();

            byte[] ivbytes = new byte[16];
            Random rand = new Random();
            rand.nextBytes(ivbytes);

            IvParameterSpec ivSpec = new IvParameterSpec(ivbytes);

            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            byte[] result = cipher.doFinal(phonenobyteContent);

            byte[] finalBytes = new byte[ivbytes.length + result.length];
            System.arraycopy(ivbytes, 0, finalBytes, 0, ivbytes.length);
            System.arraycopy(result, 0, finalBytes, ivbytes.length, result.length);

            return finalBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] decryptAES(String phoneno) {
        try {
            byte[] asBytes = Base64.getDecoder().decode(phoneno.toString().replace("\n", ""));
            byte[] iv = new byte[16];
            byte[] encryptBytes = new byte[asBytes.length - iv.length];

            System.arraycopy(asBytes, 0, iv, 0, iv.length);
            System.arraycopy(asBytes, iv.length, encryptBytes, 0, encryptBytes.length);

            SecretKeySpec key = new SecretKeySpec(mSnKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
            byte[] result = cipher.doFinal(encryptBytes);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ScoreConf reqScoreConf() throws IOException, InterruptedException {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .build();
        Request req = new Request.Builder()
                .url("http://ipdata.yazx.com/api/ipv4/user/rule/")
                .post(RequestBody.create("{\"snuser\": \"" + mSnUser + "\"}", MediaType.parse("application/json")))
                .build();

        String s = Objects.requireNonNull(httpClient.newCall(req).execute().body()).string();
        if (s.isEmpty()) {
            return null;
        }
        Gson gson = new Gson();
        HashMap hashMap = gson.fromJson(s, HashMap.class);
        String data = (String) hashMap.get("data");
        byte[] decryptAES = decryptAES(data);
        return gson.fromJson(new String(decryptAES), ScoreConf.class);
    }
}
