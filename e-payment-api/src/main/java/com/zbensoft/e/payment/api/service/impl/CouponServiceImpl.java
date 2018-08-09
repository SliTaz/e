package com.zbensoft.e.payment.api.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.CouponService;
import com.zbensoft.e.payment.db.domain.ConsumerCoupon;
import com.zbensoft.e.payment.db.domain.ConsumerFamilyCoupon;
import com.zbensoft.e.payment.db.domain.ConsumerFamilyGroupFamilyKey;
import com.zbensoft.e.payment.db.domain.ConsumerGroup;
import com.zbensoft.e.payment.db.domain.ConsumerGroupUserKey;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.Coupon;
import com.zbensoft.e.payment.db.domain.CouponBuy;
import com.zbensoft.e.payment.db.domain.CouponUserAndFamily;
import com.zbensoft.e.payment.db.domain.CouponUserAndFamilySer;
import com.zbensoft.e.payment.db.domain.GoodsType;
import com.zbensoft.e.payment.db.domain.GoodsUnit;
import com.zbensoft.e.payment.db.mapper.ConsumerCouponMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerFamilyCouponMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerFamilyGroupFamilyMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerGroupMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerGroupUserMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerUserClapMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerUserMapper;
import com.zbensoft.e.payment.db.mapper.CouponBuyMapper;
import com.zbensoft.e.payment.db.mapper.CouponMapper;
import com.zbensoft.e.payment.db.mapper.GoodsTypeMapper;
import com.zbensoft.e.payment.db.mapper.GoodsUnitMapper;

@Service
public class CouponServiceImpl implements CouponService {

	@Autowired
	CouponMapper couponMapper;

	@Autowired
	ConsumerUserMapper consumerUserMapper;
	@Autowired
	ConsumerUserClapMapper consumerUserClapMapper;
	@Autowired
	ConsumerFamilyCouponMapper consumerFamilyCouponMapper;
	@Autowired
	ConsumerFamilyGroupFamilyMapper consumerFamilyGroupFamilyMapper;
	@Autowired
	ConsumerCouponMapper consumerCouponMapper;

	@Autowired
	ConsumerGroupMapper consumerGroupMapper;

	@Autowired
	ConsumerGroupUserMapper consumerGroupUserMapper;
	@Autowired
	CouponBuyMapper couponBuyMapper;
	@Autowired
	GoodsUnitMapper goodsUnitMapper;
	@Autowired
	GoodsTypeMapper goodsTypeMapper;

	@Override
	public int deleteByPrimaryKey(String couponId) {
		return couponMapper.deleteByPrimaryKey(couponId);
	}

	@Override
	public int insert(Coupon record) {
		return couponMapper.insert(record);
	}

	@Override
	public int insertSelective(Coupon record) {
		return couponMapper.insertSelective(record);
	}

	@Override
	public Coupon selectByPrimaryKey(String couponId) {
		return couponMapper.selectByPrimaryKey(couponId);
	}

	@Override
	public int updateByPrimaryKeySelective(Coupon record) {
		return couponMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(Coupon record) {
		return couponMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<Coupon> selectPage(Coupon record) {
		return couponMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		couponMapper.deleteAll();
	}

	@Override
	public int count(Coupon coupon) {
		return couponMapper.count(coupon);
	}

	@Override
	public ResponseRestEntity<Coupon> updateStatus(String id) {

		Coupon coupon = couponMapper.selectByPrimaryKey(id);
		if (coupon == null) {
			return new ResponseRestEntity<Coupon>(HttpRestStatus.NOT_FOUND);
		}
		if (coupon.getStatus().toString().equals("1")) {
			// 改变用户状态 0:启用 1:停用
			coupon.setStatus(0);

			if (MessageDef.GET_TYPE.GRANT == coupon.getGetType() && MessageDef.GRANT_TYPE.UNGRANT == coupon.getGrandType()) {
				if (MessageDef.GET_LIMIT.CONSUMER == coupon.getGetLimit()) {
					if (coupon.getConsumerGroupId() != null) {
						ConsumerGroup consumerGroup = consumerGroupMapper.selectByPrimaryKey(coupon.getConsumerGroupId());
						if (consumerGroup != null) {
							// 消费用户组
							List<ConsumerGroupUserKey> list = consumerGroupUserMapper.selectByGroupId(consumerGroup.getConsumerGroupId());
							if (list != null && list.size() > 0) {
								for (ConsumerGroupUserKey bean : list) {
									if (bean.getUserId() != null) {
										List<ConsumerUserClap> consumerUserClapList = consumerUserClapMapper.selectByUserId(bean.getUserId());
										if (consumerUserClapList != null && consumerUserClapList.size() > 0) {
											for (ConsumerUserClap consumerUserClapBean : consumerUserClapList) {
												ConsumerCoupon consumerCoupon = new ConsumerCoupon();
												consumerCoupon.setConsumerUserClapId(consumerUserClapBean.getConsumerUserClapId());
												consumerCoupon.setCouponId(coupon.getCouponId());
												consumerCoupon.setStatus(MessageDef.COUPON_STATUS.UNUSE);
												consumerCouponMapper.insert(consumerCoupon);
											}

										}
									}
								}
							}

						}
					}
				} else if (MessageDef.GET_LIMIT.FAMILY == coupon.getGetLimit()) {
					// 家庭组
					if (coupon.getConsumerGroupId() != null) {
						List<ConsumerFamilyGroupFamilyKey> familyList = consumerFamilyGroupFamilyMapper.selectByGroupId(coupon.getConsumerGroupId());
						if (familyList != null && familyList.size() > 0) {
							for (ConsumerFamilyGroupFamilyKey familyBean : familyList) {
								ConsumerFamilyCoupon beans = new ConsumerFamilyCoupon();
								beans.setFamilyId(familyBean.getFamilyId());
								beans.setCouponId(coupon.getCouponId());
								beans.setStatus(MessageDef.COUPON_STATUS.UNUSE);
								consumerFamilyCouponMapper.insert(beans);
							}
						}

					}
				} else if (MessageDef.GET_LIMIT.STORE_FAMILY == coupon.getGetLimit()) {// store Coupon 关联插入

					if (coupon.getConsumerGroupId() != null && !"".equals(coupon.getConsumerGroupId())) {// getConsumerGroupId 这里的只就是clapStoreNo
						Set<String> familySet = new HashSet<String>();
						List<ConsumerUserClap> consumerUserClapResultList = consumerUserClapMapper.selectByClapStoreNo(coupon.getConsumerGroupId());
						if (consumerUserClapResultList != null && consumerUserClapResultList.size() > 0) {
							for (ConsumerUserClap consumerUserClapResult : consumerUserClapResultList) {
								familySet.add(consumerUserClapResult.getFamilyId());
							}
						}
						if (familySet != null && familySet.size() > 0) {
							for (String familyId : familySet) {
								ConsumerFamilyCoupon consumerFamilyCoupon = new ConsumerFamilyCoupon();
								consumerFamilyCoupon.setCouponId(coupon.getCouponId());
								consumerFamilyCoupon.setFamilyId(familyId);
								consumerFamilyCoupon.setStatus(MessageDef.COUPON_STATUS.UNUSE);
								consumerFamilyCouponMapper.insert(consumerFamilyCoupon);
							}
						}
					}
				}

			}
			coupon.setGrandType(MessageDef.GRANT_TYPE.GRANT);
		}
		couponMapper.updateStatus(coupon);

		return new ResponseRestEntity<Coupon>(HttpRestStatus.OK);
	}

	@Override
	public ResponseRestEntity<List<CouponUserAndFamily>> getConsumercoupon(String userId, String userName, int status, int limitType, String start, String length) {
		if (userId == null || userId.isEmpty()) {
			ConsumerUser consumerUser = consumerUserMapper.selectByUserName(userName);
			if (consumerUser == null) {
				return new ResponseRestEntity<List<CouponUserAndFamily>>(HttpRestStatus.NOTEMPTY, "用户不存在");
			}
			userId = consumerUser.getUserId();

		}
		if (MessageDef.GET_LIMIT.CONSUMER != limitType && MessageDef.GET_LIMIT.FAMILY != limitType) {
			return new ResponseRestEntity<List<CouponUserAndFamily>>(HttpRestStatus.NOT_EXIST, "类型不存在");
		}
		List<ConsumerUserClap> list = consumerUserClapMapper.selectByUserId(userId);

		if (list != null && list.size() > 0) {
			if (MessageDef.GET_LIMIT.CONSUMER == limitType) {
				List<CouponUserAndFamily> couponUserAndFamilyList = new ArrayList<>();
				for (ConsumerUserClap consumerUserClap : list) {
					ConsumerCoupon consumerCouponSer = new ConsumerCoupon();
					consumerCouponSer.setConsumerUserClapId(consumerUserClap.getConsumerUserClapId());
					consumerCouponSer.setStatus(status);
					int pageNum = PageHelperUtil.getPageNum(start, length);
					int pageSize = PageHelperUtil.getPageSize(start, length);
					PageHelper.startPage(pageNum, pageSize);
					List<ConsumerCoupon> userCouponList = consumerCouponMapper.selectByStatus(consumerCouponSer);
					if (userCouponList != null && userCouponList.size() > 0) {
						for (ConsumerCoupon consumerCoupon : userCouponList) {
							CouponUserAndFamilySer couponUserAndFamilySer = new CouponUserAndFamilySer();
							couponUserAndFamilySer.setCouponId(consumerCoupon.getCouponId());
							// couponUserAndFamilySer.setStatus(status);
							CouponUserAndFamily couponUserAndFamily = couponMapper.selectConsumerCoupon(couponUserAndFamilySer);
							if (couponUserAndFamily != null) {
								couponUserAndFamily.setConsumerUserClapId(consumerUserClap.getConsumerUserClapId());
								couponUserAndFamily.setCouponStatus(consumerCoupon.getStatus());
								if (StringUtils.isNotEmpty(couponUserAndFamily.getGoodId())) {
									GoodsType type = goodsTypeMapper.selectByPrimaryKey(couponUserAndFamily.getGoodId());
									if (type != null) {
										couponUserAndFamily.setGoodName(type.getName());
									}
								}
								if (couponUserAndFamily != null) {
									couponUserAndFamilyList.add(couponUserAndFamily);
								}
							}
						}
					}

				}

				if (couponUserAndFamilyList != null && couponUserAndFamilyList.size() > 0) {
					for (CouponUserAndFamily couponUserAndFamily : couponUserAndFamilyList) {
						// if (MessageDef.COUPON_STATUS.EXPAIN == status
						// && MessageDef.COUPON_STATUS.EXPAIN != couponUserAndFamily.getCouponStatus()) {
						// ConsumerCoupon couponUpdate = new ConsumerCoupon();
						// couponUpdate.setConsumerUserClapId(couponUserAndFamily.getConsumerUserClapId());
						// couponUpdate.setCouponId(couponUserAndFamily.getCouponId());
						// couponUpdate.setStatus(MessageDef.COUPON_STATUS.EXPAIN);
						// consumerCouponMapper.updateByPrimaryKeySelective(couponUpdate);
						// couponUserAndFamily.setCouponStatus(MessageDef.COUPON_STATUS.EXPAIN);
						// }
						if (MessageDef.COUPON_TYPE.BUY == couponUserAndFamily.getType()) {
							CouponBuy couponBuy = couponBuyMapper.selectByPrimaryKey(couponUserAndFamily.getCouponId());
							if (couponBuy != null) {
								couponUserAndFamily.setGoodCount(couponBuy.getCount());
								GoodsUnit goodsUnit = goodsUnitMapper.selectByPrimaryKey(couponBuy.getGoodUnitId());
								if (goodsUnit != null) {
									couponUserAndFamily.setGoodUnitName(goodsUnit.getName());
								}
							}
						}
					}
					return new ResponseRestEntity<List<CouponUserAndFamily>>(couponUserAndFamilyList, HttpRestStatus.OK);
				}

			} else if (MessageDef.GET_LIMIT.FAMILY == limitType) {
				List<CouponUserAndFamily> couponUserAndFamilyList = new ArrayList<>();
				for (ConsumerUserClap consumerUserClap : list) {
					ConsumerFamilyCoupon consumerFamilyCouponSer = new ConsumerFamilyCoupon();
					if (consumerUserClap.getFamilyId() == null || "".equals(consumerUserClap.getFamilyId())) {
						break;
					}
					consumerFamilyCouponSer.setFamilyId(consumerUserClap.getFamilyId());
					consumerFamilyCouponSer.setStatus(status);
					int pageNum = PageHelperUtil.getPageNum(start, length);
					int pageSize = PageHelperUtil.getPageSize(start, length);
					PageHelper.startPage(pageNum, pageSize);
					List<ConsumerFamilyCoupon> familyCouponList = consumerFamilyCouponMapper.selectByStatus(consumerFamilyCouponSer);
					if (familyCouponList != null && familyCouponList.size() > 0) {
						for (ConsumerFamilyCoupon consumerFamilyCoupon : familyCouponList) {
							CouponUserAndFamilySer couponUserAndFamilySer = new CouponUserAndFamilySer();
							couponUserAndFamilySer.setCouponId(consumerFamilyCoupon.getCouponId());
							// couponUserAndFamilySer.setStatus(status);
							CouponUserAndFamily couponUserAndFamily = couponMapper.selectConsumerCoupon(couponUserAndFamilySer);
							if (couponUserAndFamily != null) {
								couponUserAndFamily.setFamilyId(consumerUserClap.getFamilyId());
								couponUserAndFamily.setCouponStatus(consumerFamilyCoupon.getStatus());
								if (StringUtils.isNotEmpty(couponUserAndFamily.getGoodId())) {
									GoodsType type = goodsTypeMapper.selectByPrimaryKey(couponUserAndFamily.getGoodId());
									if (type != null) {
										couponUserAndFamily.setGoodName(type.getName());
									}
								}
								if (couponUserAndFamily != null) {
									couponUserAndFamilyList.add(couponUserAndFamily);
								}
							}
						}
					}

				}

				if (couponUserAndFamilyList != null && couponUserAndFamilyList.size() > 0) {
					for (CouponUserAndFamily couponUserAndFamily : couponUserAndFamilyList) {
						// if (MessageDef.COUPON_STATUS.EXPAIN == status && MessageDef.COUPON_STATUS.EXPAIN != couponUserAndFamily.getCouponStatus()) {
						// ConsumerFamilyCoupon consumerFamilyCouponUpdate = new ConsumerFamilyCoupon();
						// consumerFamilyCouponUpdate.setFamilyId(couponUserAndFamily.getFamilyId());
						// consumerFamilyCouponUpdate.setCouponId(couponUserAndFamily.getCouponId());
						// consumerFamilyCouponUpdate.setStatus(MessageDef.COUPON_STATUS.EXPAIN);
						// consumerFamilyCouponMapper.updateByPrimaryKeySelective(consumerFamilyCouponUpdate);
						// couponUserAndFamily.setCouponStatus(MessageDef.COUPON_STATUS.EXPAIN);
						// }
						if (MessageDef.COUPON_TYPE.BUY == couponUserAndFamily.getType()) {
							CouponBuy couponBuy = couponBuyMapper.selectByPrimaryKey(couponUserAndFamily.getCouponId());
							if (couponBuy != null) {
								couponUserAndFamily.setGoodCount(couponBuy.getCount());
								GoodsUnit goodsUnit = goodsUnitMapper.selectByPrimaryKey(couponBuy.getGoodUnitId());
								if (goodsUnit != null) {
									couponUserAndFamily.setGoodUnitName(goodsUnit.getName());
								}
							}
						}
					}
					return new ResponseRestEntity<List<CouponUserAndFamily>>(couponUserAndFamilyList, HttpRestStatus.OK);
				}

				// return new ResponseRestEntity<List<CouponUserAndFamily>>(couponUserAndFamilyList, HttpRestStatus.OK);
			}
		}
		return new ResponseRestEntity<List<CouponUserAndFamily>>(new ArrayList<>(), HttpRestStatus.OK);
	}

	@Override
	public List<Coupon> selectExpiredCoupon(Coupon couponSer) {

		return couponMapper.selectExpiredCoupon(couponSer);
	}

	@Override
	@Transactional(value = "DataSourceManager")
	public void insertCouponFamilyCoupon(Coupon toDBcoupon, List<ConsumerFamilyCoupon> toDBConsumerFamilyCouponList) {
		if (toDBcoupon != null) {
			couponMapper.insert(toDBcoupon);
			if (toDBConsumerFamilyCouponList != null && toDBConsumerFamilyCouponList.size() > 0) {
				for (ConsumerFamilyCoupon consumerFamilyCoupon : toDBConsumerFamilyCouponList) {
					consumerFamilyCouponMapper.insert(consumerFamilyCoupon);
				}
			}
		}
	}

	@Override
	public List<Coupon> selectAvailableCoupon(Coupon newCouponSer) {
		return couponMapper.selectAvailableCoupon(newCouponSer);
	}

	@Override
	@Transactional(value = "DataSourceManager")
	public void updateWithFamilyCoupon(Coupon existCoupon) {
		couponMapper.updateByPrimaryKey(existCoupon);
		ConsumerFamilyCoupon delConsumerFamilyCoupon = new ConsumerFamilyCoupon();
		delConsumerFamilyCoupon.setCouponId(existCoupon.getCouponId());
		consumerFamilyCouponMapper.deleteByCouponId(delConsumerFamilyCoupon);

	}

}
