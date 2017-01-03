package com.codeest.geeknews.presenter;

import com.codeest.geeknews.base.RxPresenter;
import com.codeest.geeknews.model.bean.NodeListBean;
import com.codeest.geeknews.model.bean.RealmLikeBean;
import com.codeest.geeknews.model.bean.RepliesListBean;
import com.codeest.geeknews.model.db.RealmHelper;
import com.codeest.geeknews.model.http.RetrofitHelper;
import com.codeest.geeknews.presenter.contract.RepliesContract;
import com.codeest.geeknews.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by codeest on 16/12/23.
 */

public class RepliesPresenter extends RxPresenter<RepliesContract.View> implements RepliesContract.Presenter{

    private RetrofitHelper mRetrofitHelper;
    private RealmHelper mRealmHelper;

    @Inject
    public RepliesPresenter(RetrofitHelper mRetrofitHelper, RealmHelper mRealmHelper) {
        this.mRetrofitHelper = mRetrofitHelper;
        this.mRealmHelper = mRealmHelper;
    }

    @Override
    public void getContent(String topic_id) {
        Subscription rxSubscription = mRetrofitHelper.fetchRepliesList(topic_id)
                .compose(RxUtil.<List<RepliesListBean>>rxSchedulerHelper())
                .subscribe(new Action1<List<RepliesListBean>>() {
                    @Override
                    public void call(List<RepliesListBean> repliesListBeen) {
                        mView.showContent(repliesListBeen);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mView.showError("获取数据失败");
                    }
                });
        addSubscrebe(rxSubscription);
    }

    @Override
    public void getTopInfo(String topic_id) {
        Subscription rxSubscription = mRetrofitHelper.fetchTopicInfo(topic_id)
                .compose(RxUtil.<List<NodeListBean>>rxSchedulerHelper())
                .filter(new Func1<List<NodeListBean>, Boolean>() {
                    @Override
                    public Boolean call(List<NodeListBean> nodeListBeen) {
                        return nodeListBeen.size() > 0;
                    }
                })
                .map(new Func1<List<NodeListBean>, NodeListBean>() {
                    @Override
                    public NodeListBean call(List<NodeListBean> nodeListBeen) {
                        return nodeListBeen.get(0);
                    }
                })
                .subscribe(new Action1<NodeListBean>() {
                    @Override
                    public void call(NodeListBean nodeListBean) {
                        mView.showTopInfo(nodeListBean);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mView.showError("获取数据失败");
                    }
                });
        addSubscrebe(rxSubscription);
    }

    @Override
    public void insert(RealmLikeBean bean) {
        mRealmHelper.insertLikeBean(bean);
    }

    @Override
    public void delete(String id) {
        mRealmHelper.deleteLikeBean(id);
    }

    @Override
    public boolean query(String id) {
        return mRealmHelper.queryLikeId(id);
    }
}
