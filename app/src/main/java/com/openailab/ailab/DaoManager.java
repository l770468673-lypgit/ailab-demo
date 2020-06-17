package com.openailab.ailab;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.openailab.ailab.dao.UserInfos;


import java.util.List;

public class DaoManager {

    /**
     * Helper
     */
    private DaoMaster.DevOpenHelper mHelper;//获取Helper对象
    /**
     * 数据库
     */
    private SQLiteDatabase db;
    /**
     * DaoMaster
     */
    private DaoMaster mDaoMaster;
    /**
     * DaoSession
     */
    public static DaoSession mDaoSession;
    /**
     * 上下文
     */
    private Context context;
    /**
     * dao
     */


    private static DaoManager mDbController;
    private UserInfosDao mUserInfosDao;

    /**
     * 获取单例
     *
     * @param context
     */
    public static DaoManager getInstance(Context context) {
        if (mDbController == null) {
            synchronized (DaoManager.class) {
                if (mDbController == null) {
                    mDbController = new DaoManager(context);
                }
            }
        }
        return mDbController;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public DaoManager(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, "userinfos.db", null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
        mUserInfosDao = mDaoSession.getUserInfosDao();
    }


    public  static DaoSession getDaoSession() {
        return mDaoSession;
    }

    /**
     * 获取可写数据库
     *
     * @return
     */
    private SQLiteDatabase getWritableDatabase() {
        if (mHelper == null) {
            mHelper = new DaoMaster.DevOpenHelper(context, "userinfos.db", null);
        }
        SQLiteDatabase db = mHelper.getWritableDatabase();
        return db;
    }


    /**
     * 会自动判定是插入还是替换
     *
     * @param UserInfos
     */
    public void insertOrReplace(UserInfos UserInfos) {
        mUserInfosDao.insertOrReplace(UserInfos);
    }
    /**
     * 更新数据
     * @param personInfor
     */
    public void update(UserInfos personInfor,String s){
        UserInfos mOldPersonInfor = mUserInfosDao.queryBuilder().where(UserInfosDao.Properties._userid.eq(personInfor.get_userid())).build().unique();//拿到之前的记录
        if(mOldPersonInfor !=null){
            mOldPersonInfor.setName(s);
            mUserInfosDao.update(mOldPersonInfor);
        }
    }
    /**
     * 按条件查询数据
     */
    public List<UserInfos> searchByWhere(String wherecluse){
        List<UserInfos>personInfors = (List<UserInfos>) mUserInfosDao.queryBuilder().where(UserInfosDao.Properties._userid.eq(wherecluse)).build().unique();
        return personInfors;
    }
    /**
     * 查询所有数据
     */
    public List<UserInfos> searchAll(){
        List<UserInfos>personInfors=mUserInfosDao.queryBuilder().list();
        return personInfors;
    }
    /**
     * 删除数据
     */
    public void delete(String wherecluse){
        mUserInfosDao.queryBuilder().where(UserInfosDao.Properties.Name.eq(wherecluse)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

}
