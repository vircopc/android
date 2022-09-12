package com.example.stickyheadergridview;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.stickyheadergridview.RefreshableView.PullToRefreshListener;

public class GalleryActivity extends Activity implements CallBackInterface, View.OnClickListener {
    private ProgressDialog mProgressDialog;
    private GridView mGridView;
    private List<GridItem> mGirdList = new ArrayList<GridItem>();
    private static int section = 1;
    private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
    StickyGridAdapter mStickyGridAdapter;
    TextView mVedioImageNum, mCheckNum;
    int mImageSize, mVedioSize;
    LinearLayout mTitleView, mEditView, mDeleteView, mDeleteCheck;
    ImageView mExitEdit, mCheckAll, mCheckAllBg;
    ImageView mBack;
    RefreshableView mRefreshableView;
    private Boolean mCheckALLState = false;
    private boolean mReLoading = false;
    private List<GridItem> deleteList = new ArrayList<GridItem>();

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:
                    mProgressDialog.dismiss();
                    mStickyGridAdapter.list = mGirdList;
                    mStickyGridAdapter.notifyDataSetChanged();
                    updateImageVedioSum();
                    mVedioImageNum.setText(String.format(getString(R.string.image_vedio_num), mImageSize, mVedioSize));
                    break;
                case 0x02:
                    mProgressDialog.dismiss();
                    mStickyGridAdapter.notifyDataSetChanged();
                    updateImageVedioSum();
                    handleExit();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity_main);
        Window window =getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        );
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        startThread();
        findView();
        mStickyGridAdapter = new StickyGridAdapter(GalleryActivity.this, mGirdList, mGridView, this);
        mGridView.setAdapter(mStickyGridAdapter);
        setClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReLoading == true) {
            startThread();
            mReLoading = false;
        }
    }

    @Override
    public void callBackFunction() {
        updateCheckNum();
    }


    private void findView() {
        mVedioImageNum = findViewById(R.id.vedio_image_num);
        mTitleView = findViewById(R.id.title_view);
        mEditView = findViewById(R.id.edit_view);
        mExitEdit = findViewById(R.id.exit_edit);
        mCheckAll = findViewById(R.id.check_all);
        mCheckAllBg = findViewById(R.id.check_all_bg);
        mCheckNum = findViewById(R.id.check_num);
        mGridView = findViewById(R.id.asset_grid);
        mDeleteCheck = findViewById(R.id.delete_check);
        mDeleteView = findViewById(R.id.delete_view);
        mRefreshableView = findViewById(R.id.refreshable_view);
        mBack = findViewById(R.id.bt_back);
    }


    private void startThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mGirdList = FileUtil.getAllFile();
                Collections.sort(mGirdList, new YMComparator());
                setSection();
                Message msg = mHandler.obtainMessage();
                msg.what = 0x01;
                mHandler.sendMessage(msg);
            }
        }).start();
        mProgressDialog = ProgressDialog.show(GalleryActivity.this, null, getString(R.string.loading_file));
    }

    private void setSection() {
        for (ListIterator<GridItem> it = mGirdList.listIterator(); it.hasNext(); ) {
            GridItem mGridItem = it.next();
            String ym = mGridItem.getTime();
            if (!sectionMap.containsKey(ym)) {
                mGridItem.setSection(section);
                sectionMap.put(ym, section);
                section++;
            } else {
                mGridItem.setSection(sectionMap.get(ym));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exit_edit:
                handleExit();
                break;
            case R.id.bt_back:
                finish();
                break;
            case R.id.check_all:
                mCheckALLState = !mCheckALLState;
                updateCheckAll();
                for (int i = 0; i < mStickyGridAdapter.list.size(); i++) {
                    mStickyGridAdapter.list.get(i).setChecked(mCheckALLState);
                }
                mStickyGridAdapter.notifyDataSetChanged();
                mCheckNum.setText(String.format(getString(R.string.check_number), mCheckALLState ? mStickyGridAdapter.list.size() : 0));
                mDeleteCheck.setEnabled(mCheckALLState ? true : false);
                break;
            case R.id.delete_check:
                showDeleteDialog();
                break;


        }

    }

    private void setClickListener() {
        mExitEdit.setOnClickListener(this);
        mCheckAll.setOnClickListener(this);
        mDeleteCheck.setOnClickListener(this);
        mBack.setOnClickListener(this);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (mStickyGridAdapter.isEditMode == false) {
                    Intent intent ;
                    File file = new File(mStickyGridAdapter.list.get(position).getPath());
                    if (mStickyGridAdapter.list.get(position).isVedio()) {
                        intent= new Intent(getApplicationContext(),showVedioActivity.class);
                        intent.putExtra("VEDIO_PATH",mStickyGridAdapter.list.get(position).getPath());
                    } else {
                        intent= new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "image/*");
                    }
                    startActivity(intent);
                    mReLoading = true;
                } else {
                    boolean check = !mStickyGridAdapter.list.get(position).isChecked();
                    mStickyGridAdapter.list.get(position).setChecked(check);
                    mStickyGridAdapter.notifyDataSetChanged();
                    updateCheckNum();
                }
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mStickyGridAdapter.isEditMode == false) {
                    mStickyGridAdapter.isEditMode = true;
                    mEditView.setVisibility(View.VISIBLE);
                    mDeleteView.setVisibility(View.VISIBLE);
                    mTitleView.setVisibility(View.GONE);

                    boolean check = !mStickyGridAdapter.list.get(position).isChecked();
                    mStickyGridAdapter.list.get(position).setChecked(check);
                    mStickyGridAdapter.notifyDataSetChanged();
                    updateCheckNum();
                }
                return true;
            }
        });

        mRefreshableView.setOnRefreshListener(new PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mRefreshableView.finishRefreshing();
            }

            @Override
            public boolean getEditMode() {
                return mStickyGridAdapter.isEditMode;
            }
        }, 0);

    }

    private void updateCheckAll() {
        if (mCheckALLState) {
            mCheckAllBg.setImageResource(R.drawable.check_all_bg_selected);
            mCheckAll.setImageResource(R.drawable.check_all_selected);
        } else {
            mCheckAllBg.setImageResource(R.drawable.check_all_bg_default);
            mCheckAll.setImageResource(R.drawable.check_all_default);
        }
    }

    public void deleteFileFromDatabase(Context context, GridItem gridItem) {
        ContentResolver mContentResolver = context.getContentResolver();
        Uri uri;
        String where;
        if (gridItem.isVedio()) {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            where = MediaStore.Video.Media.DATA + "='" + gridItem.getPath() + "'";
        } else {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            where = MediaStore.Images.Media.DATA + "='" + gridItem.getPath() + "'";
        }
        mContentResolver.delete(uri, where, null);
    }

    private void showDeleteDialog() {
        deleteList.clear();
        for (GridItem gridItem : mStickyGridAdapter.list) {
            if (gridItem.isChecked()) {
                deleteList.add(gridItem);
            }
        }
        String message = getMessageString();
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_gallery, null);
        final TextView cancel = view.findViewById(R.id.cancel);
        final TextView sure = view.findViewById(R.id.sure);
        final TextView tv_message = view.findViewById(R.id.message);
        tv_message.setText(message);
        new BlurDialog(this, false) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(view);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteList.clear();
                        dismiss();
                    }
                });
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleDelete();
                        dismiss();
                    }
                });
            }
        }.show();

    }

    @NonNull
    private String getMessageString() {
        String message;
        if (deleteList.size() == 1) {
            if (deleteList.get(0).isVedio()) {
                message = getString(R.string.delete_this_vedio);
            } else {
                message = getString(R.string.delete_this_photo);
            }
        } else {
            int i = 0;
            for (GridItem gridItem : deleteList) {
                if (gridItem.isVedio())
                    break;
                i++;
            }
            if (i == deleteList.size()) {
                message = String.format(getResources().getString(R.string.delete_size_photo), deleteList.size());
            } else {
                message = String.format(getResources().getString(R.string.delete_size_vedio_photo), deleteList.size());
            }
        }
        return message;
    }

    private void handleDelete() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (GridItem gridItem : deleteList) {
                    deleteFileFromDatabase(getApplicationContext(), gridItem);
                }
                mStickyGridAdapter.list.removeAll(deleteList);
                deleteList.clear();
                Message msg = mHandler.obtainMessage();
                msg.what = 0x02;
                mHandler.sendMessage(msg);
            }
        }).start();
        mProgressDialog = ProgressDialog.show(GalleryActivity.this, null, getString(R.string.deleting));
    }

    private void updateImageVedioSum() {
        int i = 0;
        for (GridItem gridItem : mStickyGridAdapter.list) {
            if (MediaFile.isImageFileType(gridItem.getPath())) {
                i++;
            }
        }
        mImageSize = i;
        mVedioSize = mStickyGridAdapter.list.size() - i;
        mVedioImageNum.setText(String.format(getString(R.string.image_vedio_num), mImageSize, mVedioSize));
    }

    private void handleExit() {
        mStickyGridAdapter.isEditMode = false;
        mCheckALLState = false;
        updateCheckAll();
        mEditView.setVisibility(View.GONE);
        mDeleteView.setVisibility(View.GONE);
        mTitleView.setVisibility(View.VISIBLE);
        for (int i = 0; i < mStickyGridAdapter.list.size(); i++) {
            mStickyGridAdapter.list.get(i).setChecked(false);
        }
        mStickyGridAdapter.notifyDataSetChanged();
    }


    public void updateCheckNum() {
        int i = 0;
        for (GridItem gridItem : mStickyGridAdapter.list) {
            if (gridItem.isChecked()) {
                i++;
            }
        }
        mCheckNum.setText(String.format(getString(R.string.check_number), i));
        mCheckALLState = (i == mStickyGridAdapter.list.size() ? true : false);
        updateCheckAll();
        mDeleteCheck.setEnabled(i == 0 ? false : true);
    }


    @Override
    public void onBackPressed() {
        if (mStickyGridAdapter.isEditMode == true) {
            handleExit();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mGridView.removeAllViewsInLayout();
        mGridView = null;
        mGirdList.clear();
        super.onDestroy();
    }
}
