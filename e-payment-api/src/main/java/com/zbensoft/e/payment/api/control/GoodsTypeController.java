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

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.GoodsTypeService;
import com.zbensoft.e.payment.db.domain.GoodsType;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/goodsType")
@RestController
public class GoodsTypeController {
	@Autowired
	GoodsTypeService goodsTypeService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_COUPON_GT_Q')")
	@ApiOperation(value = "Query GoodsType，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<GoodsType>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		GoodsType goodsType = new GoodsType();
		goodsType.setGoodId(id);
		goodsType.setName(name);
		goodsType.setDeleteFlag(0);
		int count = goodsTypeService.count(goodsType);
		if (count == 0) {
			return new ResponseRestEntity<List<GoodsType>>(new ArrayList<GoodsType>(), HttpRestStatus.NOT_FOUND);
		}
		List<GoodsType> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = goodsTypeService.selectPage(goodsType);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = goodsTypeService.selectPage(goodsType);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<GoodsType>>(new ArrayList<GoodsType>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<GoodsType>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_COUPON_GT_Q')")
	@ApiOperation(value = "Query GoodsType", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<GoodsType> selectByPrimaryKey(@PathVariable("id") String id) {
		GoodsType goodsType = goodsTypeService.selectByPrimaryKey(id);
		if (goodsType == null) {
			return new ResponseRestEntity<GoodsType>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<GoodsType>(goodsType, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_COUPON_GT_E')")
	@ApiOperation(value = "Add GoodsType", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createGoodsType(@Valid @RequestBody GoodsType goodsType, BindingResult result, UriComponentsBuilder ucBuilder) {
		//goodsType.setGoodId(System.currentTimeMillis()+"");
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		GoodsType bean = goodsTypeService.selectByPrimaryKey(goodsType.getGoodId());
		goodsType.setDeleteFlag(0);
		if (goodsTypeService.isGoodsTypeExist(goodsType) || bean !=null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		
		goodsTypeService.insert(goodsType);
		//新增日志
        //CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, goodsType,CommonLogImpl.SYSTEM_MANAGE);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/goodsType/{id}").buildAndExpand(goodsType.getGoodId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_COUPON_GT_E')")
	@ApiOperation(value = "Edit GoodsType", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<GoodsType> updateGoodsType(@PathVariable("id") String id, @Valid @RequestBody GoodsType goodsType, BindingResult result) {

		GoodsType currentGoodsType = goodsTypeService.selectByPrimaryKey(id);

		if (currentGoodsType == null) {
			return new ResponseRestEntity<GoodsType>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentGoodsType.setName(goodsType.getName());
		currentGoodsType.setDeleteFlag(goodsType.getDeleteFlag());
		currentGoodsType.setParentGoodId(goodsType.getParentGoodId());
		currentGoodsType.setRemark(goodsType.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<GoodsType>(currentGoodsType,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		goodsTypeService.updateByPrimaryKey(currentGoodsType);
		//修改日志
       //CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentGoodsType,CommonLogImpl.SYSTEM_MANAGE);
		return new ResponseRestEntity<GoodsType>(currentGoodsType, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_COUPON_GT_E')")
	@ApiOperation(value = "Edit Part GoodsType", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<GoodsType> updateGoodsTypeSelective(@PathVariable("id") String id, @RequestBody GoodsType goodsType) {

		GoodsType currentGoodsType = goodsTypeService.selectByPrimaryKey(id);

		if (currentGoodsType == null) {
			return new ResponseRestEntity<GoodsType>(HttpRestStatus.NOT_FOUND);
		}
		currentGoodsType.setGoodId(id);
		currentGoodsType.setName(goodsType.getName());
		currentGoodsType.setDeleteFlag(goodsType.getDeleteFlag());
		currentGoodsType.setParentGoodId(goodsType.getParentGoodId());
		currentGoodsType.setRemark(goodsType.getRemark());
		goodsTypeService.updateByPrimaryKeySelective(currentGoodsType);
		//修改日志
	     //CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentGoodsType,CommonLogImpl.SYSTEM_MANAGE);
		return new ResponseRestEntity<GoodsType>(currentGoodsType, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_COUPON_GT_E')")
	@ApiOperation(value = "Delete GoodsType", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<GoodsType> deleteGoodsType(@PathVariable("id") String id) {

		GoodsType goodsType = goodsTypeService.selectByPrimaryKey(id);
		if (goodsType == null) {
			return new ResponseRestEntity<GoodsType>(HttpRestStatus.NOT_FOUND);
		}
		goodsType.setDeleteFlag(1);
		goodsTypeService.updateByPrimaryKeySelective(goodsType);
		//删除日志开始
/*		GoodsType delBean = new GoodsType();
		delBean.setGoodId(id);
		delBean.setDeleteFlag(1);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.SYSTEM_MANAGE);*/
		//删除日志结束
		return new ResponseRestEntity<GoodsType>(HttpRestStatus.NO_CONTENT);
	}
}