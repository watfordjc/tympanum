package uk.johncook.android.tympanum.data;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import java.lang.ref.WeakReference;
import java.util.List;

public class Ostn15Repository {
    private final WeakReference<Context> context;
    private final MutableLiveData<List<Ostn15Point>> searchResults = new MutableLiveData<>();
    private Ostn15Database db;
    private Ostn15Dao ostn15Dao;

    public Ostn15Repository(Application application) {
        context = new WeakReference<>(application.getApplicationContext());
        db = Ostn15Database.getInstance(context.get());
        if (db != null) {
            ostn15Dao = db.ostn15Dao();
        }
    }

    private void asyncFinished(List<Ostn15Point> results) {
        searchResults.setValue(results);
    }

    private static class QueryAsyncTask extends AsyncTask<Integer, Void, List<Ostn15Point>> {
        private final Ostn15Dao asyncTaskDao;
        private Ostn15Repository delegate = null;

        QueryAsyncTask(Ostn15Dao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected List<Ostn15Point> doInBackground(Integer... integers) {
            return asyncTaskDao.loadAllByIds(integers);
        }

        @Override
        protected void onPostExecute(List<Ostn15Point> ostn15Points) {
            delegate.asyncFinished(ostn15Points);
        }
    }

    public void getOstn15Points(Integer[] integers) {
        if (ostn15Dao == null) {
            if (db == null) {
                db = Ostn15Database.getInstance(context.get());
            }
            if (db != null && db.ostn15Dao() != null) {
                ostn15Dao = db.ostn15Dao();
            } else {
                return;
            }
        }
        QueryAsyncTask asyncTask = new QueryAsyncTask(ostn15Dao);
        asyncTask.delegate = this;
        asyncTask.execute(integers);
    }

    public MutableLiveData<List<Ostn15Point>> getSearchResults() {
        return searchResults;
    }
}
