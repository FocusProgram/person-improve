package com.jeecg.p3.goldeneggs.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.jeecgframework.p3.core.util.plugin.ContextHolderUtils;
import org.jeecgframework.p3.core.utils.common.PageList;
import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.common.PageQueryWrapper;
import org.jeecgframework.p3.core.utils.common.Pagenation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeecg.p3.baseApi.service.BaseApiActTxtService;
import com.jeecg.p3.goldeneggs.dao.WxActGoldeneggsAwardsDao;
import com.jeecg.p3.goldeneggs.dao.WxActGoldeneggsDao;
import com.jeecg.p3.goldeneggs.dao.WxActGoldeneggsPrizesDao;
import com.jeecg.p3.goldeneggs.dao.WxActGoldeneggsRelationDao;
import com.jeecg.p3.goldeneggs.def.SystemGoldProperties;
import com.jeecg.p3.goldeneggs.entity.WxActGoldeneggs;
import com.jeecg.p3.goldeneggs.entity.WxActGoldeneggsAwards;
import com.jeecg.p3.goldeneggs.entity.WxActGoldeneggsPrizes;
import com.jeecg.p3.goldeneggs.entity.WxActGoldeneggsRelation;
import com.jeecg.p3.goldeneggs.service.WxActGoldeneggsService;


@Service("wxActGoldeneggsService")
public class WxActGoldeneggsServiceImpl implements WxActGoldeneggsService {
    @Resource
    private WxActGoldeneggsDao wxActGoldeneggsDao;
    @Autowired
    private BaseApiActTxtService baseApiActTxtService;
    @Autowired
    private WxActGoldeneggsRelationDao wxActGoldeneggsRelationDao;
    @Resource
    private WxActGoldeneggsAwardsDao wxActGoldeneggsAwardsDao;
    @Resource
    private WxActGoldeneggsPrizesDao wxActGoldeneggsPrizesDao;

    private static String defaultJwid = SystemGoldProperties.defaultJwid;
    private static String oldActCode = SystemGoldProperties.oldActCode;


    public final static Logger log = LoggerFactory.getLogger(WxActGoldeneggsServiceImpl.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doAdd(WxActGoldeneggs wxActGoldeneggs) {
        wxActGoldeneggs.setTemplateCode("hd0921");
        wxActGoldeneggs.setProjectCode("goldeneggs");
        wxActGoldeneggsDao.insert(wxActGoldeneggs);
        List<WxActGoldeneggsRelation> awardsList = wxActGoldeneggs.getAwarsList();
        if (awardsList != null) {
            for (WxActGoldeneggsRelation wxActGoldeneggsRelation : awardsList) {
                //--update-begin--date:2018-3-27 18:14:03 author:liwenhui for:????????????????????????,???????????????
                if (StringUtils.isEmpty(wxActGoldeneggsRelation.getAwardId())) {
                    wxActGoldeneggsRelation.setAwardId(saveAwards(wxActGoldeneggsRelation.getAwardName()));
                } else {
                    WxActGoldeneggsAwards wxActGoldeneggsAwards = wxActGoldeneggsAwardsDao.get(wxActGoldeneggsRelation.getAwardId());
                    //??????awardId???awardName???????????????????????????????????????
                    if (!wxActGoldeneggsAwards.getAwardsName().equals(wxActGoldeneggsRelation.getAwardName())) {
                        wxActGoldeneggsRelation.setAwardId(saveAwards(wxActGoldeneggsRelation.getAwardName()));
                    }
                }
                //--update-end--date:2018-3-27 18:14:03 author:liwenhui for:????????????????????????,???????????????
                //--update-begin--date:2018-3-27 18:14:03 author:liwenhui for:????????????????????????,???????????????
                if (StringUtils.isEmpty(wxActGoldeneggsRelation.getPrizeId())) {
                    wxActGoldeneggsRelation.setPrizeId(savePrizes(wxActGoldeneggsRelation.getPrizeName(), wxActGoldeneggsRelation.getAwardImg()));
                } else {
                    WxActGoldeneggsPrizes wxActGoldeneggsPrizes = wxActGoldeneggsPrizesDao.get(wxActGoldeneggsRelation.getPrizeId());
                    //prizeId???prizeName???????????????????????????????????????
                    log.info("------????????????----wxActGoldeneggsPrizes:" + wxActGoldeneggsPrizes.getName());
                    log.info("------??????????????????----wxActGoldeneggsRelation:" + wxActGoldeneggsRelation.getPrizeName());
                    if (!wxActGoldeneggsPrizes.getName().equals(wxActGoldeneggsRelation.getPrizeName())) {
                        wxActGoldeneggsRelation.setPrizeId(savePrizes(wxActGoldeneggsRelation.getPrizeName(), wxActGoldeneggsRelation.getAwardImg()));
                    }
                    log.info("------????????????----wxActGoldeneggsPrizes:" + wxActGoldeneggsPrizes.getImg());
                    log.info("------??????????????????----wxActGoldeneggsRelation:" + wxActGoldeneggsRelation.getAwardImg());
                    //prizeId???prizeName??????img???AwardImg?????????????????????????????????????????????
                    if (StringUtils.isNotEmpty(wxActGoldeneggsRelation.getAwardImg()) && StringUtils.isNotEmpty(wxActGoldeneggsPrizes.getImg())) {
                        if (wxActGoldeneggsPrizes.getName().equals(wxActGoldeneggsRelation.getPrizeName()) && !wxActGoldeneggsPrizes.getImg().equals(wxActGoldeneggsRelation.getAwardImg())) {
                            wxActGoldeneggsRelation.setPrizeId(savePrizes(wxActGoldeneggsRelation.getPrizeName(), wxActGoldeneggsRelation.getAwardImg()));
                        }
                    }
                }
                //--update-end--date:2018-3-27 18:14:03 author:liwenhui for:????????????????????????,???????????????


                wxActGoldeneggsRelation.setActId(wxActGoldeneggs.getId());
                wxActGoldeneggsRelation.setRemainNum(wxActGoldeneggsRelation.getAmount());
                if (wxActGoldeneggsRelation.getProbability() == null) {
                    wxActGoldeneggsRelation.setProbability(new BigDecimal("0"));
                    //update-begin-zhangweijian-----Date:20180830----for:????????????
                } else {
                    BigDecimal pencet = new BigDecimal("100");
                    wxActGoldeneggsRelation.setProbability(wxActGoldeneggsRelation.getProbability().divide(pencet));
                    //update-end-zhangweijian-----Date:20180830----for:????????????
                }
            }
            for (WxActGoldeneggsRelation wxActGoldeneggsRelation : awardsList) {
                wxActGoldeneggsRelationDao.insert(wxActGoldeneggsRelation);
            }
        }

        baseApiActTxtService.copyActText(oldActCode, wxActGoldeneggs.getId());

    }

    /**
     * @param awardName
     * @return
     * @throws Exception
     * @??????:????????????
     * @??????:liwenhui
     * @??????:2018-3-28 ??????09:56:47
     * @?????????
     */
    private String saveAwards(String awardName) {
        WxActGoldeneggsAwards wxActGoldeneggsAwards = new WxActGoldeneggsAwards();
        String jwid = ContextHolderUtils.getSession().getAttribute("jwid").toString();
        String createBy = ContextHolderUtils.getSession().getAttribute("system_userid").toString();
        if (defaultJwid.equals(jwid)) {
            //??????????????????????????????
            List<WxActGoldeneggsAwards> queryAwardsByName = wxActGoldeneggsAwardsDao.queryAwardsByName(jwid, createBy, awardName);
            if (queryAwardsByName.size() > 0) {
                return queryAwardsByName.get(0).getId();
            }
        } else {
            //??????????????????????????????
            List<WxActGoldeneggsAwards> queryAwardsByName = wxActGoldeneggsAwardsDao.queryAwardsByName(jwid, "", awardName);
            if (queryAwardsByName.size() > 0) {
                return queryAwardsByName.get(0).getId();
            }
        }
        wxActGoldeneggsAwards.setCreateBy(createBy);
        wxActGoldeneggsAwards.setJwid(jwid);
        wxActGoldeneggsAwards.setAwardsName(awardName);
        wxActGoldeneggsAwardsDao.insert(wxActGoldeneggsAwards);
        return wxActGoldeneggsAwards.getId();
    }


    /**
     * @param prizeName
     * @return
     * @??????:????????????
     * @??????:liwenhui
     * @??????:2018-3-28 ??????10:00:09
     * @?????????
     */
    private String savePrizes(String prizeName, String prizeImg) {
        WxActGoldeneggsPrizes wxActGoldeneggsPrizes = new WxActGoldeneggsPrizes();
        String jwid = ContextHolderUtils.getSession().getAttribute("jwid").toString();
        String createBy = ContextHolderUtils.getSession().getAttribute("system_userid").toString();
        //??????????????????????????????
        List<WxActGoldeneggsPrizes> queryPrizesByName = wxActGoldeneggsPrizesDao.queryPrizesByName(jwid, createBy, prizeName);
        if (queryPrizesByName.size() > 0) {
            queryPrizesByName.get(0).setImg(prizeImg);
            wxActGoldeneggsPrizesDao.update(queryPrizesByName.get(0));
            return queryPrizesByName.get(0).getId();
        }
        wxActGoldeneggsPrizes.setCreateBy(createBy);
        wxActGoldeneggsPrizes.setJwid(jwid);
        wxActGoldeneggsPrizes.setName(prizeName);
        wxActGoldeneggsPrizes.setImg(prizeImg);
        wxActGoldeneggsPrizesDao.insert(wxActGoldeneggsPrizes);
        return wxActGoldeneggsPrizes.getId();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String doEdit(WxActGoldeneggs wxActGoldeneggs) {
        String msg = "";
        try {
            wxActGoldeneggsDao.update(wxActGoldeneggs);
            List<WxActGoldeneggsRelation> newAwardsList = wxActGoldeneggs.getAwarsList();//????????????????????????
            List<String> ids = new ArrayList<String>();
            if (newAwardsList != null) {
                for (WxActGoldeneggsRelation relation : newAwardsList) {

                    //--update-begin--date:2018-3-27 18:14:03 author:liwenhui for:????????????????????????,???????????????
                    if (StringUtils.isEmpty(relation.getAwardId())) {
                        relation.setAwardId(saveAwards(relation.getAwardName()));
                    } else {
                        WxActGoldeneggsAwards wxActGoldeneggsAwards = wxActGoldeneggsAwardsDao.get(relation.getAwardId());
                        //??????awardId???awardName???????????????????????????????????????
                        if (!wxActGoldeneggsAwards.getAwardsName().equals(relation.getAwardName())) {
                            relation.setAwardId(saveAwards(relation.getAwardName()));
                        }
                    }
                    //--update-end--date:2018-3-27 18:14:03 author:liwenhui for:????????????????????????,???????????????
                    //--update-begin--date:2018-3-27 18:14:03 author:liwenhui for:????????????????????????,???????????????
                    if (StringUtils.isEmpty(relation.getPrizeId())) {
                        relation.setPrizeId(savePrizes(relation.getPrizeName(), relation.getAwardImg()));
                    } else {
                        WxActGoldeneggsPrizes wxActGoldeneggsPrizes = wxActGoldeneggsPrizesDao.get(relation.getPrizeId());
                        //prizeId???prizeName???????????????????????????????????????
                        if (!wxActGoldeneggsPrizes.getName().equals(relation.getPrizeName())) {
                            relation.setPrizeId(savePrizes(relation.getPrizeName(), relation.getAwardImg()));
                        }
                        //prizeId???prizeName??????img???AwardImg?????????????????????????????????????????????
                        if (StringUtils.isNotEmpty(relation.getAwardImg()) && StringUtils.isNotEmpty(wxActGoldeneggsPrizes.getImg())) {
                            if (wxActGoldeneggsPrizes.getName().equals(relation.getPrizeName()) && !wxActGoldeneggsPrizes.getImg().equals(relation.getAwardImg())) {
                                relation.setPrizeId(savePrizes(relation.getPrizeName(), relation.getAwardImg()));
                            }
                        }
                    }
                    //--update-end--date:2018-3-27 18:14:03 author:liwenhui for:????????????????????????,???????????????

                    if (StringUtils.isNotEmpty(relation.getId())) {
                        ids.add(relation.getId());
                    }
                }

                //????????????????????????
                for (WxActGoldeneggsRelation wxActGoldeneggsRelation : newAwardsList) {
                    //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    if (org.jeecgframework.p3.core.utils.common.StringUtils.isNotEmpty(wxActGoldeneggsRelation.getId())) {
                        WxActGoldeneggsRelation relation = wxActGoldeneggsRelationDao.get(wxActGoldeneggsRelation.getId());
                        Integer outNum = relation.getAmount() - relation.getRemainNum();
                        if (wxActGoldeneggsRelation.getAmount() < outNum) {
                            WxActGoldeneggsAwards awards = wxActGoldeneggsAwardsDao.get(relation.getAwardId());
                            msg = msg + awards.getAwardsName() + "??????????????????????????????????????? ???" + outNum + ";</br>";
                            return msg;
                        } else {
                            Integer newRemainNum = 0;
                            if (wxActGoldeneggsRelation.getAmount() > relation.getAmount()) {
                                newRemainNum = wxActGoldeneggsRelation.getAmount() - relation.getAmount() + relation.getRemainNum();
                            } else {
                                newRemainNum = relation.getRemainNum() - (relation.getAmount() - wxActGoldeneggsRelation.getAmount());
                            }
                            wxActGoldeneggsRelation.setRemainNum(newRemainNum);
                        }
                    }
                }

                wxActGoldeneggsRelationDao.bactchDeleteOldAwards(ids, wxActGoldeneggs.getId());//???????????????????????????????????????????????????
                for (WxActGoldeneggsRelation wxActGoldeneggsRelation : newAwardsList) {
                    //update-begin-zhangweijian-----Date:20180830----for:????????????
                    if (wxActGoldeneggsRelation.getProbability() == null) {
                        wxActGoldeneggsRelation.setProbability(new BigDecimal("0"));
                    } else {
                        BigDecimal pencet = new BigDecimal("100");
                        wxActGoldeneggsRelation.setProbability(wxActGoldeneggsRelation.getProbability().divide(pencet));
                    }
                    //update-end-zhangweijian-----Date:20180830----for:????????????
                    if (StringUtils.isEmpty(wxActGoldeneggsRelation.getId())) {
                        wxActGoldeneggsRelation.setActId(wxActGoldeneggs.getId());
                        wxActGoldeneggsRelation.setRemainNum(wxActGoldeneggsRelation.getAmount());
                        wxActGoldeneggsRelationDao.insert(wxActGoldeneggsRelation);
                    } else {
                        wxActGoldeneggsRelationDao.update(wxActGoldeneggsRelation);
                    }
                }
            } else {
                wxActGoldeneggsRelationDao.bactchDeleteOldAwards(ids, wxActGoldeneggs.getId());//???????????????????????????????????????????????????
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }


    @Override
    public void doDelete(String id) {
        wxActGoldeneggsDao.delete(id);
        wxActGoldeneggsRelationDao.batchDeleteByActId(id);//????????????????????????
        baseApiActTxtService.batchDeleteByActCode(id);//????????????????????????
    }

    @Override
    public WxActGoldeneggs queryById(String id) {
        WxActGoldeneggs wxActGoldeneggs = wxActGoldeneggsDao.get(id);
        return wxActGoldeneggs;
    }

    @Override
    public PageList<WxActGoldeneggs> queryPageList(
            PageQuery<WxActGoldeneggs> pageQuery) {
        PageList<WxActGoldeneggs> result = new PageList<WxActGoldeneggs>();
        Integer itemCount = wxActGoldeneggsDao.count(pageQuery);
        PageQueryWrapper<WxActGoldeneggs> wrapper = new PageQueryWrapper<WxActGoldeneggs>(pageQuery.getPageNo(), pageQuery.getPageSize(), itemCount, pageQuery.getQuery());
        List<WxActGoldeneggs> list = wxActGoldeneggsDao.queryPageList(wrapper);
        Pagenation pagenation = new Pagenation(pageQuery.getPageNo(), itemCount, pageQuery.getPageSize());
        result.setPagenation(pagenation);
        result.setValues(list);
        return result;
    }

    @Override
    public void editShortUrl(String id, String shortUrl) {
        wxActGoldeneggsDao.editShortUrl(id, shortUrl);
    }


}
