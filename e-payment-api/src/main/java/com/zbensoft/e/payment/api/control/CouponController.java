package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.ConsumerCouponService;
import com.zbensoft.e.payment.api.service.api.ConsumerFamilyCouponService;
import com.zbensoft.e.payment.api.service.api.ConsumerFamilyGroupService;
import com.zbensoft.e.payment.api.service.api.ConsumerGroupService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.api.service.api.CouponBuyService;
import com.zbensoft.e.payment.api.service.api.CouponService;
import com.zbensoft.e.payment.api.service.api.GoodsTypeService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.db.domain.ConsumerFamilyGroup;
import com.zbensoft.e.payment.db.domain.ConsumerGroup;
import com.zbensoft.e.payment.db.domain.Coupon;
import com.zbensoft.e.payment.db.domain.CouponBuy;
import com.zbensoft.e.payment.db.domain.CouponUserAndFamily;
import com.zbensoft.e.payment.db.domain.GoodsType;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.ZTreeNode;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/coupon")
@RestController
public class CouponController {
	@Autowired
	CouponService couponService;

	@Autowired
	GoodsTypeService goodsTypeService;

	@Autowired
	ConsumerGroupService consumerGroupService;

	@Autowired
	ConsumerFamilyGroupService consumerFamilyGroupService;
	@Autowired
	MerchantUserService merchantUserService;
	// 购买券
	@Autowired
	CouponBuyService couponBuyService;

	// 消费用户
	@Autowired
	ConsumerUserService consumerUserService;

	@Autowired
	ConsumerUserClapService consumerUserClapService;

	@Autowired
	ConsumerCouponService consumerCouponService;
	@Autowired
	ConsumerFamilyCouponService consumerFamilyCouponService;

	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询券，支持分页
	@PreAuthorize("hasRole('R_COUPON_Q')")
	@ApiOperation(value = "Query Coupon，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<Coupon>> selectPage(@RequestParam(required = false) String id, @RequestParam(required = false) String goodId, @RequestParam(required = false) String name,
			@RequestParam(required = false) Integer type, @RequestParam(required = false) Integer getLimit, @RequestParam(required = false) Integer status, @RequestParam(required = false) Integer getType,
			@RequestParam(required = false) Integer grandType, @RequestParam(required = false) String userStartTime, @RequestParam(required = false) String userEndTime, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		Coupon coupon = new Coupon();
		coupon.setCouponId(id);
		coupon.setGoodId(goodId);
		coupon.setName(name);

		coupon.setType(type);

		coupon.setGetLimit(getLimit);

		coupon.setStatus(status);

		coupon.setGetType(getType);

		coupon.setGrandType(grandType);

		coupon.setTimeStart(userStartTime);
		coupon.setTimeEnd(userEndTime);
		List<Coupon> list = new ArrayList<Coupon>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = couponService.selectPage(coupon);

		} else {
			list = couponService.selectPage(coupon);
		}

		int count = couponService.count(coupon);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<Coupon>>(new ArrayList<Coupon>(), HttpRestStatus.NOT_FOUND);
		}
		List<Coupon> listNew = new ArrayList<Coupon>();
		for (Coupon bean : list) {
			//if(bean.getGoodId()!=null && !bean.getGoodId().isEmpty()){
			GoodsType goodsType = goodsTypeService.selectByPrimaryKey(bean.getGoodId());
			if (bean.getConsumerGroupId() != null && !bean.getConsumerGroupId().isEmpty()) {
				if (bean.getGetLimit().toString().equals("0")) {
					ConsumerGroup consumerGroup = consumerGroupService.selectByPrimaryKey(bean.getConsumerGroupId());
					if (consumerGroup != null) {
						bean.setGroupName(consumerGroup.getName());
					}
				} else if (bean.getGetLimit().toString().equals("1")) {
					ConsumerFamilyGroup consumerFamilyGroup = consumerFamilyGroupService.selectByPrimaryKey(bean.getConsumerGroupId());
					if (consumerFamilyGroup != null) {
						bean.setGroupName(consumerFamilyGroup.getName());
					}
				}else if (bean.getGetLimit().toString().equals("2")) {
					MerchantUser merchantUser = merchantUserService.selectByClapId(bean.getConsumerGroupId());
					if (merchantUser != null) {
						bean.setGroupName(merchantUser.getUserName());
						bean.setClapStoreNo(bean.getConsumerGroupId());
						bean.setMerchantName(merchantUser.getUserName());
					}
				}
				
				MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(bean.getConsumerGroupId());
				if(merchantUser!=null){
					bean.setClapStoreNo(merchantUser.getUserName());
				}
				
			}
			if (coupon != null) {
				if(goodsType!=null){
				bean.setGoodTypeName(goodsType.getName());
				}
			}
			//}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<Coupon>>(listNew, HttpRestStatus.OK, count, count);
	}

	// 查询券
	@PreAuthorize("hasRole('R_COUPON_Q')")
	@ApiOperation(value = "Query Coupon", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<Coupon> selectByPrimaryKey(@PathVariable("id") String id) {
		Coupon coupon = couponService.selectByPrimaryKey(id);
		if (coupon == null) {
			return new ResponseRestEntity<Coupon>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<Coupon>(coupon, HttpRestStatus.OK);
	}

	// 新增券
	@PreAuthorize("hasRole('R_COUPON_E')")
	@ApiOperation(value = "Add Coupon", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@Valid @RequestBody Coupon coupon, BindingResult result, UriComponentsBuilder ucBuilder) {

		coupon.setCouponId(IDGenerate.generateCommOne(IDGenerate.COUPON));
		coupon.setCreateTime(PageHelperUtil.getCurrentDate());
		if(coupon.getGetLimit().toString().equals(MessageDef.GET_LIMIT.STORE_FAMILY_STRING)){
			coupon.setConsumerGroupId(coupon.getClapStoreNo());
		}else{
			coupon.setConsumerGroupId(coupon.getConsumerGroupId());
		}
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		couponService.insert(coupon);
		//新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, coupon,CommonLogImpl.COUPON_MANAGE);
		// 根据类型判断 1:购买券
		switch (coupon.getType()) {
		case MessageDef.COUPON_TYPE.BUY:
			CouponBuy bean = new CouponBuy();
			bean.setCouponId(coupon.getCouponId());
			bean.setGoodUnitId(coupon.getGoodUnitId());
			bean.setCount(coupon.getCount());
			couponBuyService.insert(bean);
			break;
		case MessageDef.COUPON_TYPE.GENERATE:
/*			CouponBuy couponBuyBean = new CouponBuy();
			couponBuyBean.setCouponId(coupon.getCouponId());
			couponBuyBean.setGoodUnitId(coupon.getGoodUnitId());
			couponBuyBean.setCount(coupon.getCount());
			couponBuyService.insert(couponBuyBean);*/
			break;
		default:
			break;
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/coupon/{id}").buildAndExpand(coupon.getCouponId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改券信息
	@PreAuthorize("hasRole('R_COUPON_E')")
	@ApiOperation(value = "Edit Coupon", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<Coupon> update(@PathVariable("id") String id, @Valid @RequestBody Coupon coupon, BindingResult result) {

		Coupon currentCoupon = couponService.selectByPrimaryKey(id);

		if (currentCoupon == null) {
			return new ResponseRestEntity<Coupon>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentCoupon.setGoodId(coupon.getGoodId());
		currentCoupon.setName(coupon.getName());
		currentCoupon.setType(coupon.getType());
		if(coupon.getGetLimit().toString().equals(MessageDef.GET_LIMIT.STORE_FAMILY_STRING)){
			currentCoupon.setConsumerGroupId(coupon.getClapStoreNo());
		}else{
			currentCoupon.setConsumerGroupId(coupon.getConsumerGroupId());
		}
		currentCoupon.setGetLimit(coupon.getGetLimit());
		currentCoupon.setStatus(coupon.getStatus());
		currentCoupon.setGetType(coupon.getGetType());
		currentCoupon.setUserStartTime(coupon.getUserStartTime());
		currentCoupon.setUserEndTime(coupon.getUserEndTime());
		currentCoupon.setAmount(coupon.getAmount());
		currentCoupon.setGrandType(coupon.getGrandType());
		currentCoupon.setRemark(coupon.getRemark());

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Coupon>(currentCoupon, HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		couponService.updateByPrimaryKey(currentCoupon);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentCoupon,CommonLogImpl.COUPON_MANAGE);
		// 根据类型判断 1:购买券 (修改操作)
		switch (coupon.getType()) {
		case MessageDef.COUPON_TYPE.BUY:
			CouponBuy couponBuy = couponBuyService.selectByPrimaryKey(currentCoupon.getCouponId());
			
			CouponBuy bean = new CouponBuy();
			bean.setCouponId(currentCoupon.getCouponId());
			bean.setGoodUnitId(coupon.getGoodUnitId());
			bean.setCount(coupon.getCount());
			
			if (couponBuy != null) {
				couponBuyService.updateByPrimaryKey(bean);
			}else{
				couponBuyService.insert(bean);
			}
			
			break;
		case MessageDef.COUPON_TYPE.GENERATE:
/*			CouponBuy couponBuyBean = new CouponBuy();
			couponBuyBean.setCouponId(currentCoupon.getCouponId());
			couponBuyBean.setGoodUnitId(coupon.getGoodUnitId());
			couponBuyBean.setCount(coupon.getCount());
			couponBuyService.updateByPrimaryKey(couponBuyBean);*/
			break;
		default:
			break;
		}

		return new ResponseRestEntity<Coupon>(currentCoupon, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分券信息
	@PreAuthorize("hasRole('R_COUPON_E')")
	@ApiOperation(value = "Edit Part Coupon", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<Coupon> updateSelective(@PathVariable("id") String id, @RequestBody Coupon coupon) {

		Coupon currentCoupon = couponService.selectByPrimaryKey(id);

		if (currentCoupon == null) {
			return new ResponseRestEntity<Coupon>(HttpRestStatus.NOT_FOUND);
		}
		currentCoupon.setCouponId(id);
		currentCoupon.setGoodId(coupon.getGoodId());
		currentCoupon.setConsumerGroupId(coupon.getConsumerGroupId());
		currentCoupon.setName(coupon.getName());
		currentCoupon.setType(coupon.getType());
		currentCoupon.setGetLimit(coupon.getGetLimit());
		currentCoupon.setStatus(coupon.getStatus());
		currentCoupon.setGetType(coupon.getGetType());
		currentCoupon.setUserStartTime(coupon.getUserStartTime());
		currentCoupon.setUserEndTime(coupon.getUserEndTime());
		currentCoupon.setGrandType(coupon.getGrandType());
		currentCoupon.setRemark(coupon.getRemark());
		couponService.updateByPrimaryKeySelective(coupon);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, coupon,CommonLogImpl.COUPON_MANAGE);
		return new ResponseRestEntity<Coupon>(currentCoupon, HttpRestStatus.OK);
	}

	// 删除指定券
	@PreAuthorize("hasRole('R_COUPON_E')")
	@ApiOperation(value = "Delete Coupon", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<Coupon> delete(@PathVariable("id") String id) {

		Coupon coupon = couponService.selectByPrimaryKey(id);
		if (coupon == null) {
			return new ResponseRestEntity<Coupon>(HttpRestStatus.NOT_FOUND);
		}

		couponService.deleteByPrimaryKey(id);
		//删除日志开始
		Coupon coupo = new Coupon();
		coupo.setConsumerGroupId(id);
				    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, coupo,CommonLogImpl.COUPON_MANAGE);
					//删除日志结束
		// 根据类型判断 1:购买券 (删除操作)
		switch (coupon.getType()) {
		case MessageDef.COUPON_TYPE.BUY:
			couponBuyService.deleteByPrimaryKey(id);
			break;
		default:
			break;
		}
		return new ResponseRestEntity<Coupon>(HttpRestStatus.NO_CONTENT);
	}
	// 用户启用
	@PreAuthorize("hasRole('R_COUPON_E')")
	@ApiOperation(value = "enable the specified Coupon", notes = "")
	@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<Coupon> enableCoupon(@PathVariable("id") String id) {
		//日志开始
		Coupon couponLogBean = new Coupon();
		couponLogBean.setCouponId(id);
		couponLogBean.setStatus(MessageDef.STATUS.ENABLE_INT);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, couponLogBean,CommonLogImpl.COUPON_MANAGE);
		//日志结束
		return couponService.updateStatus(id);

	}

	// 用户停用
	@PreAuthorize("hasRole('R_COUPON_E')")
	@ApiOperation(value = "enable the specified Coupon", notes = "")
	@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<Coupon> disableCoupon(@PathVariable("id") String id) {

		Coupon coupon = couponService.selectByPrimaryKey(id);
		if (coupon == null) {
			return new ResponseRestEntity<Coupon>(HttpRestStatus.NOT_FOUND);
		}
		// 改变用户状态 0:启用 1:停用
		coupon.setStatus(1);
		couponService.updateByPrimaryKey(coupon);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, coupon,CommonLogImpl.COUPON_MANAGE);
		return new ResponseRestEntity<Coupon>(HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_COUPON_Q')")
	@RequestMapping(value = "/selectGoods", method = RequestMethod.GET)
	public ResponseEntity<Coupon> selectGoods(@RequestParam(required = false) String goodId) {
		// System.out.println("goodId:"+goodId);

		List<GoodsType> goodsTypeLists = goodsTypeService.findAll();
		List<ZTreeNode> zTreeNodeList = new ArrayList<ZTreeNode>();

		for (GoodsType g : goodsTypeLists) {
			ZTreeNode zTreeNode = new ZTreeNode();
			zTreeNode.setId(g.getGoodId());
			zTreeNode.setpId(g.getParentGoodId());
			zTreeNode.setName(g.getName());

			if (CommonFun.isEmpty(goodId)) {// 为空
				zTreeNode.setChecked(false);// 未选中
			} else {
				if (goodId.equals(g.getGoodId())) {
					zTreeNode.setChecked(true);// 选中
				} else {
					zTreeNode.setChecked(false);// 未选中
				}
			}

			zTreeNodeList.add(zTreeNode);
		}

		Coupon coupon = new Coupon();
		coupon.setzTreeNodes(zTreeNodeList);
		return new ResponseEntity<Coupon>(coupon, HttpStatus.OK);
	}

	// 查询有效未使用的券
	@PreAuthorize("hasRole('R_COUPON_Q') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query Effective Coupon", notes = "")
	@RequestMapping(value = "/getConsumercoupon", method = RequestMethod.GET)
	public ResponseRestEntity<List<CouponUserAndFamily>> getConsumercoupon(@RequestParam(required = false) String userId, @RequestParam(required = false) String userName, @RequestParam int status,
			@RequestParam int limitType, @RequestParam String start, @RequestParam String length) {
		
		return couponService.getConsumercoupon(userId,userName, status,limitType,start, length);
		
	}
}