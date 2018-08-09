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
import com.zbensoft.e.payment.api.service.api.SysUserClapService;
import com.zbensoft.e.payment.db.domain.SysUserClap;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/userClap")
@RestController
public class UserClapController {
	@Autowired
	SysUserClapService sysUserClapService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	// 查询用户clap卡，支持分页
	@ApiOperation(value = "Query the user clap card, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<SysUserClap>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String cardNo, @RequestParam(required = false) Integer type,
			@RequestParam(required = false) String remark, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		SysUserClap userClap = new SysUserClap();
		userClap.setUserId(id);
		userClap.setCardNo(cardNo);
		userClap.setType(type);
		userClap.setRemark(remark);

		List<SysUserClap> list = sysUserClapService.selectPage(userClap);
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = sysUserClapService.selectPage(userClap);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = sysUserClapService.selectPage(userClap);
		}

		int count = sysUserClapService.count(userClap);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<SysUserClap>>(new ArrayList<SysUserClap>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<SysUserClap>>(list, HttpRestStatus.OK, count, count);
	}

	// 查询用户clap卡
	@ApiOperation(value = "Query the user clap card", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<SysUserClap> selectByPrimaryKey(@PathVariable("id") String id) {
		SysUserClap sysUserClap = sysUserClapService.selectByPrimaryKey(id);
		if (sysUserClap == null) {
			return new ResponseRestEntity<SysUserClap>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<SysUserClap>(sysUserClap, HttpRestStatus.OK);
	}

	// 新增用户clap卡
	@ApiOperation(value = "Add user clap card", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createUserClap(@Valid @RequestBody SysUserClap userClap, BindingResult result,
			UriComponentsBuilder ucBuilder) {

		userClap.setUserId(System.currentTimeMillis() + "");

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		sysUserClapService.insert(userClap);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/userClap/{id}").buildAndExpand(userClap.getUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.patrimonycard.add"));
	}

	// 修改用户clap卡信息
	@ApiOperation(value = "Modify the user clap card information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<SysUserClap> updateUserClap(@PathVariable("id") String id,
			@Valid @RequestBody SysUserClap userClap, BindingResult result) {

		SysUserClap currentUserClap = sysUserClapService.selectByPrimaryKey(id);

		if (currentUserClap == null) {
			return new ResponseRestEntity<SysUserClap>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("bookkeepking.update.not_found.message"));
		}
		currentUserClap.setCardNo(userClap.getCardNo());

		currentUserClap.setType(userClap.getType());
		currentUserClap.setRemark(userClap.getRemark());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<SysUserClap>(currentUserClap, HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		sysUserClapService.updateByPrimaryKey(currentUserClap);

		return new ResponseRestEntity<SysUserClap>(currentUserClap, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.patrimonycard.update"));
	}

	// 修改部分用户clap卡信息
	@ApiOperation(value = "Modify some of the user clap card information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<SysUserClap> updateUserClapSelective(@PathVariable("id") String id,
			@RequestBody SysUserClap userClap) {

		SysUserClap currentUserClap = sysUserClapService.selectByPrimaryKey(id);

		if (currentUserClap == null) {
			return new ResponseRestEntity<SysUserClap>(HttpRestStatus.NOT_FOUND);
		}
		userClap.setUserId(id);
		sysUserClapService.updateByPrimaryKeySelective(userClap);

		return new ResponseRestEntity<SysUserClap>(currentUserClap, HttpRestStatus.OK);
	}

	// 删除指定用户clap卡信息
	@ApiOperation(value = "Delete the specified user clap card information", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<SysUserClap> deleteUserClap(@PathVariable("id") String id) {

		SysUserClap userClap = sysUserClapService.selectByPrimaryKey(id);
		if (userClap == null) {
			return new ResponseRestEntity<SysUserClap>(HttpRestStatus.NOT_FOUND);
		}

		sysUserClapService.deleteByPrimaryKey(id);
		return new ResponseRestEntity<SysUserClap>(HttpRestStatus.NO_CONTENT);
	}
}