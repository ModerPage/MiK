package me.modernpage.task;

import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ExecutorService;

import me.modernpage.entity.Like;
import me.modernpage.util.Constants;

public class AddPostLike {
    private static final String TAG = "AddPostLike";

    private OnAddPostLike mCallback;
    private ExecutorService mExecutorService;
    private Handler mMainThreadHandler;
    public interface OnAddPostLike {
        void onAddPostLikeComplete(Like addedLike);
    }

    public AddPostLike(OnAddPostLike callback, ExecutorService executorService, Handler handler) {
        mCallback = callback;
        mExecutorService = executorService;
        mMainThreadHandler = handler;
    }

    public void addLike(final Like like) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                Like addedLike = doInBackground(like);
                notifyResult(addedLike);
            }
        });
    }


    private void notifyResult(Like like) {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null)
                    mCallback.onAddPostLikeComplete(like);
            }
        });
    }


    private Like doInBackground(Like like) {
        if (like == null)
            return null;
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(Constants.Network.ADDLIKE_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequestBody(like).getBytes());
            outputStream.close();

            StringBuilder result = new StringBuilder();
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                result.append(line).append("\n");
            }
            bufferedReader.close();
            Log.d(TAG, "doInBackground: result: " + result.toString());
            return getLikeFromJSON(result.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground: MalformedURLException " + e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: IOException " + e.getMessage(), e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }


    private String jsonRequestBody(Like like) {
        // likeOwner
        //      liked userId
        // likedPost
        //      liked postId
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userEmail", like.getLikeOwner().getEmail());
            jsonObject.put("postId", like.getLikedPost().getPostId());
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Like getLikeFromJSON(String jsonString) {
        try {
            JSONObject jsonLike = new JSONObject(jsonString);
            long likeId = jsonLike.getLong("likeId");
            Date likedDate = Constants.mDateFormat.parse((String) jsonLike.get("likedDate"));
            Like like = new Like();
            like.setLikeId(likeId);
            like.setLikedDate(likedDate);
            return like;
        } catch (JSONException e) {
            Log.e(TAG, "getLikeCountFromJSON: JSONException: " + e.getMessage(), e);
        } catch (ParseException e) {
            Log.e(TAG, "getLikeCountFromJSON: ParseException: " + e.getMessage(), e);
        }
        return null;
    }
}
