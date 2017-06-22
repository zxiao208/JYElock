package com.jiayang.jyelock.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jiayang.jyelock.BuildConfig;
import com.jiayang.jyelock.R;
import com.jiayang.jyelock.base.BaseActivity;
import com.jiayang.jyelock.bean.GetKey;
import com.jiayang.jyelock.bean.Key;
import com.jiayang.jyelock.bean.Token;
import com.jiayang.jyelock.bean.UserInfo;
import com.jiayang.jyelock.util.JsonDataFactory;
import com.jiayang.jyelock.util.LogUtil;
import com.jiayang.jyelock.util.ToastUtil;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheType;
import com.okhttplib.annotation.Encoding;
import com.okhttplib.annotation.RequestType;
import com.umeng.message.PushAgent;
import com.yeeloc.elocsdk.ElocSDK;
import com.yeeloc.elocsdk.data.Lock;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity {


    public static final String CLIENT_ID = "lPiWsHMvArxp";//自己申请的ID
    public static final String CLIENT_SECRET = "alaX1EkDFJ8GjTqEmFZEgGNEuCtKURXYS0q9PkSX";//自己申请的Secret

    @Bind(R.id.fromCacheTV)
    TextView fromCacheTV;
    @Bind(R.id.resultTV)
    TextView resultTV;
    /**
     * 注意：测试时请更换该地址
     */
    private String url = "https://api.yeeloc.com/open-api/user";
//  private String url = "http://192.168.120.206:8080/office/api/time?key=zhousf_key";

    private boolean isNeedDeleteCache = true;

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ElocSDK.init(this, CLIENT_ID, CLIENT_SECRET);
        ElocSDK.setDebug(BuildConfig.DEBUG);
        String password = "zhaoxiao123";
        String username ="18622358828";
        ElocSDK.bindUser(username, password);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @OnClick({
            R.id.sync_btn,
            R.id.async_btn,
            R.id.force_network_btn,
            R.id.force_cache_btn,
            R.id.network_then_cache_btn,
            R.id.cache_then_network_btn,
            R.id.ten_second_cache_btn,
            R.id.delete_cache_btn
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sync_btn://同步请求
                sync();
                break;
            case R.id.async_btn://异步请求
                async();
                break;
            case R.id.force_network_btn://仅网络
                forceNetwork();
                break;
            case R.id.force_cache_btn://仅缓存
                forceCache();
                break;
            case R.id.network_then_cache_btn://先网络再缓存
                networkThenCache();
                break;
            case R.id.cache_then_network_btn://先缓存再网络
                cacheThenNetwork();
                break;
            case R.id.ten_second_cache_btn://缓存10秒失效
                tenSecondCache();
                break;
            case R.id.delete_cache_btn://清理缓存
                Lock.unlock(lockKey);
            //    deleteCache();
                //getToken();
                break;
        }
    }


    HashMap hm1 = new HashMap<String,String>();
    Token token;
    String tokenStr;
    /**
     * 同步请求：由于不能在UI线程中进行网络请求操作，所以采用子线程方式
     * post方式的  获取token
     */
    HashMap hm = new HashMap<String,String>();
    private void sync() {
        hm.put("grant_type","password");
        hm.put("client_id","lPiWsHMvArxp");
        hm.put("client_secret","alaX1EkDFJ8GjTqEmFZEgGNEuCtKURXYS0q9PkSX");
        hm.put("username","18622358828");
        hm.put("password","zhaoxiao123");
        new Thread(new Runnable() {
            @Override
            public void run() {

                final HttpInfo info = HttpInfo.Builder()
                        .setRequestType(RequestType.POST)
                        .setUrl("http://192.168.1.119:8080/appclient/portone/prsvforcastrmtype/select")
                       // .addParams(hm)
                        .setResponseEncoding(Encoding.UTF_8)//设置服务器响应编码
                        .build();

                OkHttpUtil.getDefault(this)
                        .doPostSync(info);

                final String result = info.getRetDetail();
                Log.i("zhaoxiao",result);
//                token = new Gson().fromJson(result, Token.class);
//                tokenStr=token.getAccess_token();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTV.setText("同步请求：" + result);
                        //GSon解析
//                        LogUtil.d("MainActivity", userInfo.toString());
                        setFromCacheTV(info);
                    }
                });
            }
        }).start();
        needDeleteCache(true);
    }

    /**
     * 异步请求：回调方法可以直接操作UI
     *   请求用户token
     */
    private void getToken() {
        HashMap hm = new HashMap<String,String>();
        hm.put("grant_type","password");
        hm.put("client_id","lPiWsHMvArxp");
        hm.put("client_secret","alaX1EkDFJ8GjTqEmFZEgGNEuCtKURXYS0q9PkSX");
        hm.put("username","18622358828");
        hm.put("password","zhaoxiao123");
        OkHttpUtil.getDefault(this).doAsync(
                HttpInfo.Builder()
                        .setUrl("https://api.yeeloc.com/open-api/oauth/access_token")
                        .setRequestType(RequestType.POST)//设置请求方发
                         .addParams(hm)//添加接口参数
                        .build(),
                new com.okhttplib.callback.Callback() {
                    @Override
                    public void onFailure(HttpInfo info) throws IOException {
                        String result = info.getRetDetail();
                        resultTV.setText("异步请求失败：" + result);
                    }
                    @Override
                    public void onSuccess(HttpInfo info) throws IOException {
                        String result = info.getRetDetail();
                        resultTV.setText("异步请求成功：" + result);
                        //GSon解析
//                        UserInfo userInfo = new Gson().fromJson(result, UserInfo.class);
//                        LogUtil.d("MainActivity", userInfo.toString());
//                        setFromCacheTV(info);
                    }
                });
       // needDeleteCache(false);
    }




    /**
     * 异步请求：回调方法可以直接操作UI
     *   请求用户信息get
     */
    int a =1;
    String lockKey;
    private void async() {
        HashMap hm = new HashMap<String,String>();
        hm.put("lock_id","-1");
        hm.put("password_amount","1");
        OkHttpUtil.getDefault(this).doAsync(
                HttpInfo.Builder()
                        .setUrl("https://api.yeeloc.com/open-api/lock/password/-1/1")
                        .setRequestType(RequestType.GET)//设置请求方式
                        .addHead("Authorization","Bearer"+" "+tokenStr)//添加头参数
                     //   .addParams(hm)//添加接口参数
                        .build(),

                new com.okhttplib.callback.Callback() {
                    @Override
                    public void onFailure(HttpInfo info) throws IOException {
                        String result = info.getRetDetail();
                        resultTV.setText("异步请求失败：" + result);
                    }

                    @Override
                    public void onSuccess(HttpInfo info) throws IOException {
                        String result = info.getRetDetail();
                        Log.i("zhaoxiao",result);
                        Key key;
                        List<Key> list=JsonDataFactory.jsonToArrayList(result,Key.class);

                        Log.i("zhaoxiao",list.get(0).getPassword_str());

                        lockKey=list.get(0).getPassword_str();


                        resultTV.setText("异步请求成功：" + result);
                        //GSon解析
//                        UserInfo userInfo = new Gson().fromJson(result, UserInfo.class);
//                        LogUtil.d("MainActivity", userInfo.toString());
//                        setFromCacheTV(info);
                    }
                });
        needDeleteCache(false);
    }

    /**
     * 仅网络请求
     */
    private void forceNetwork(){
        OkHttpUtil.Builder().setCacheType(CacheType.FORCE_NETWORK).build(this)
                .doGetAsync(
                        HttpInfo.Builder().setUrl(url)
                                .setRequestType(RequestType.GET)//设置请求方式
                                .addHead("Authorization","Bearer X1Ewc1tglb9rvOAzweeSmJWYhzqFVWYAukjvbTQJ")//添加头参数
                                .build(),
                        new com.okhttplib.callback.Callback() {
                            @Override
                            public void onSuccess(HttpInfo info) throws IOException {
                                String result = info.getRetDetail();
                                resultTV.setText("FORCE_NETWORK：" + result);
                                setFromCacheTV(info);
                            }

                            @Override
                            public void onFailure(HttpInfo info) throws IOException {
                                resultTV.setText("FORCE_NETWORK：" + info.getRetDetail());
                            }
                        }
                );
        needDeleteCache(false);
    }

    /**
     * 仅缓存请求
     */
    private void forceCache(){
        OkHttpUtil.Builder().setCacheType(CacheType.FORCE_CACHE).build(this)
                .doGetAsync(
                        HttpInfo.Builder()
                                .setUrl(url)
                                .setRequestType(RequestType.GET)//设置请求方式
                                .addHead("Authorization","Bearer X1Ewc1tglb9rvOAzweeSmJWYhzqFVWYAukjvbTQJ")//添加头参数
                                .build(),
                        new com.okhttplib.callback.Callback() {
                            @Override
                            public void onSuccess(HttpInfo info) throws IOException {
                                String result = info.getRetDetail();
                                resultTV.setText("FORCE_CACHE：" + result);
                                setFromCacheTV(info);
                            }

                            @Override
                            public void onFailure(HttpInfo info) throws IOException {
                                resultTV.setText("FORCE_CACHE：" + info.getRetDetail());
                            }
                        }
                );
            needDeleteCache(true);
    }

    /**
     * 先网络再缓存：先请求网络，失败则请求缓存
     */
    private void networkThenCache() {
        OkHttpUtil.Builder().setCacheType(CacheType.NETWORK_THEN_CACHE).build(this)
                .doGetAsync(
                        HttpInfo.Builder().setUrl(url).build(),
                        new com.okhttplib.callback.Callback() {
                            @Override
                            public void onSuccess(HttpInfo info) throws IOException {
                                String result = info.getRetDetail();
                                resultTV.setText("NETWORK_THEN_CACHE：" + result);
                                setFromCacheTV(info);
                            }

                            @Override
                            public void onFailure(HttpInfo info) throws IOException {
                                resultTV.setText("NETWORK_THEN_CACHE：" + info.getRetDetail());
                            }
                        }
                );
        needDeleteCache(true);
    }

    /**
     * 先缓存再网络：先请求缓存，失败则请求网络
     */
    private void cacheThenNetwork() {
        OkHttpUtil.Builder().setCacheType(CacheType.CACHE_THEN_NETWORK).build(this)
                .doGetAsync(
                        HttpInfo.Builder().setUrl(url).build(),
                        new com.okhttplib.callback.Callback() {
                            @Override
                            public void onSuccess(HttpInfo info) throws IOException {
                                String result = info.getRetDetail();
                                resultTV.setText("CACHE_THEN_NETWORK：" + result);
                                setFromCacheTV(info);
                            }

                            @Override
                            public void onFailure(HttpInfo info) throws IOException {
                                resultTV.setText("CACHE_THEN_NETWORK：" + info.getRetDetail());
                            }
                        }
                );
        needDeleteCache(true);
    }

    /**
     * 缓存10秒失效：连续点击进行测试10秒内再次请求为缓存响应，10秒后再请求则缓存失效并进行网络请求
     */
    private void tenSecondCache(){
        //由于采用同一个url测试，需要先清理缓存
        if(isNeedDeleteCache){
            isNeedDeleteCache = false;
            OkHttpUtil.getDefault().deleteCache();
        }
        OkHttpUtil.Builder()
                .setCacheType(CacheType.CACHE_THEN_NETWORK)
                .setCacheSurvivalTime(10)//缓存存活时间为10秒
                .build(this)
                .doGetAsync(
                        HttpInfo.Builder().setUrl(url).build(),
                        new com.okhttplib.callback.Callback() {
                            @Override
                            public void onSuccess(HttpInfo info) throws IOException {
                                String result = info.getRetDetail();
                                resultTV.setText("缓存10秒失效：" + result);
                                setFromCacheTV(info);
                            }

                            @Override
                            public void onFailure(HttpInfo info) throws IOException {
                                resultTV.setText("缓存10秒失效：" + info.getRetDetail());
                            }
                        }
                );
    }


    private void needDeleteCache(boolean delete){
        isNeedDeleteCache = delete;
    }

    private void setFromCacheTV(HttpInfo info){
        fromCacheTV.setText(info.isFromCache()?"缓存请求":"网络请求");
    }

    /**
     * 清理缓存
     */
    private void deleteCache(){
        if(OkHttpUtil.getDefault().deleteCache()){
            ToastUtil.show(this,"清理缓存成功");
        }else{
            ToastUtil.show(this,"清理缓存失败");
        }
    }




    OkHttpClient mOkHttpClient;
    String myGsonStr;
    //普通的okhttp同步请求数据
    private void CheckDeveloper(){
        mOkHttpClient= new OkHttpClient();

        Request.Builder requestBuilder = new Request.Builder()
                .url("https://api.yeeloc.com/open-api/user")
                .header("Authorization","Bearer X1Ewc1tglb9rvOAzweeSmJWYhzqFVWYAukjvbTQJ")

                ;

        //可以省略，默认是GET请求
        requestBuilder.method("GET",null);
        Request request = requestBuilder.build();

        Call mcall= mOkHttpClient.newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null != response.cacheResponse()) {
                    String str = response.cacheResponse().toString();
                    Log.i("zhaoxiao", "cache---" + str);
                    myGsonStr = response.body().toString();
                } else {
                    //response.body().string();
                    String str = response.networkResponse().toString();
                    Log.i("zhaoxiao", "network---" + str);
                    myGsonStr = response.body().string();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("zhaoxiao", "network---" + myGsonStr);
                        Gson gson = new Gson();
                        UserInfo userInfo = gson.fromJson(myGsonStr,UserInfo.class);
                        Toast.makeText(getApplicationContext(), "请求成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        }

}
