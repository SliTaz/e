package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.CouponBuyService;
import com.zbensoft.e.payment.db.domain.CouponBuy;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/couponBuy")
@RestController
public class CouponBuyController {
	@Autowired
	CouponBuyService couponBuyService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_COUPON_B_Q')")
	@ApiOperation(value = "Query CouponBuy，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<CouponBuy>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String userId,@RequestParam(required = false) String status,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		CouponBuy couponBuy = new CouponBuy();
		couponBuy.setCouponId(id);
		List<CouponBuy> list = couponBuyService.selectPage(couponBuy);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<CouponBuy>>(new ArrayList<CouponBuy>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<CouponBuy>>(list, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_COUPON_B_Q')")
	@ApiOperation(value = "Query CouponBuy", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<CouponBuy> selectByPrimaryKey(@PathVariable("id") String id) {
		CouponBuy couponBuy = couponBuyService.selectByPrimaryKey(id);
		if (couponBuy == null) {
			return new ResponseRestEntity<CouponBuy>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<CouponBuy>(couponBuy, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_COUPON_B_E')")
	@ApiOperation(value = "Add CouponBuy", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createCouponBuy(@Valid @RequestBody CouponBuy couponBuy,BindingResult result, UriComponentsBuilder ucBuilder) {
		couponBuy.setCouponId(System.currentTimeMillis()+"");
		
		couponBuy.setCouponId(IDGenerate.generateCommOne(IDGenerate.COUPON_BUY));
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		couponBuyService.insert(couponBuy);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/couponBuy/{id}").buildAndExpand(couponBuy.getCouponId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_COUPON_B_E')")
	@ApiOperation(value = "Edit CouponBuy", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<CouponBuy> updateCouponBuy(@PathVariable("id") String id,@Valid @RequestBody CouponBuy couponBuy, BindingResult result) {

		CouponBuy currentCouponBuy = couponBuyService.selectByPrimaryKey(id);

		if (currentCouponBuy == null) {
			return new ResponseRestEntity<CouponBuy>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentCouponBuy.setGoodUnitId(couponBuy.getGoodUnitId());
		currentCouponBuy.setCount(couponBuy.getCount());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<CouponBuy>(currentCouponBuy,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		couponBuyService.updateByPrimaryKey(currentCouponBuy);

		return new ResponseRestEntity<CouponBuy>(currentCouponBuy, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_COUPON_B_E')")
	@ApiOperation(value = "Edit Part CouponBuy", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<CouponBuy> updateCouponBuySelective(@PathVariable("id") String id,
			@RequestBody CouponBuy couponBuy) {

		CouponBuy currentCouponBuy = couponBuyService.selectByPrimaryKey(id);

		if (currentCouponBuy == null) {
			return new ResponseRestEntity<CouponBuy>(HttpRestStatus.NOT_FOUND);
		}
		currentCouponBuy.setCouponId(id);
		currentCouponBuy.setGoodUnitId(couponBuy.getGoodUnitId());
		currentCouponBuy.setCount(couponBuy.getCount());
		couponBuyService.updateByPrimaryKeySelective(currentCouponBuy);

		return new ResponseRestEntity<CouponBuy>(currentCouponBuy, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_COUPON_B_E')")
	@ApiOperation(value = "Delete CouponBuy", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<CouponBuy> deleteCouponBuy(@PathVariable("id") String id) {

		CouponBuy couponBuy = couponBuyService.selectByPrimaryKey(id);
		if (couponBuy == null) {
			return new ResponseRestEntity<CouponBuy>(HttpRestStatus.NOT_FOUND);
		}

		couponBuyService.deleteByPrimaryKey(id);
		return new ResponseRestEntity<CouponBuy>(HttpRestStatus.NO_CONTENT);
	}
}