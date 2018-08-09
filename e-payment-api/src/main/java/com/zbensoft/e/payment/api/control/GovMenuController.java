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
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.GovMenuService;
import com.zbensoft.e.payment.api.service.api.GovUserService;
import com.zbensoft.e.payment.db.domain.GovMenu;
import com.zbensoft.e.payment.db.domain.GovMenuUserMenuResponse;
import com.zbensoft.e.payment.db.domain.GovUser;
import com.zbensoft.e.payment.db.domain.SysMenuUserMenuParam;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/govMenu")
@RestController
public class GovMenuController {
	@Autowired
	GovMenuService govMenuService;
	@Autowired
	GovUserService govUserService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_GOV_U_M_Q')")
	@ApiOperation(value = "Query GovMenu，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<GovMenu>> selectPage(@RequestParam(required = false) String id, @RequestParam(required = false) String menuId, @RequestParam(required = false) String menuKeyWord,
			@RequestParam(required = false) Integer menuSortno, @RequestParam(required = false) String menuNameCn, @RequestParam(required = false) String menuNameEn, @RequestParam(required = false) String menuNameEs, @RequestParam(required = false) String menuPic, @RequestParam(required = false) String menuType,
			@RequestParam(required = false) String preMenuId, @RequestParam(required = false) String menuProces, @RequestParam(required = false) String remark, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		GovMenu govMenu = new GovMenu();
		govMenu.setMenuId(id);
		govMenu.setMenuKeyWord(menuKeyWord);
		govMenu.setMenuSortno(menuSortno);
		govMenu.setMenuNameCn(menuNameCn);
		govMenu.setMenuNameEn(menuNameEn);
		govMenu.setMenuNameEs(menuNameEs);
		govMenu.setMenuPic(menuPic);
		govMenu.setMenuType(menuType);
		govMenu.setPreMenuId(preMenuId);
		govMenu.setMenuProces(menuProces);
		govMenu.setRemark(remark);

		int count = govMenuService.count(govMenu);
		if (count == 0) {
			return new ResponseRestEntity<List<GovMenu>>(new ArrayList<GovMenu>(), HttpRestStatus.NOT_FOUND);
		}
		List<GovMenu> list = null;
		govMenuService.selectPage(govMenu);
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = govMenuService.selectPage(govMenu);
		} else {
			list = govMenuService.selectPage(govMenu);
		}

		return new ResponseRestEntity<List<GovMenu>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_GOV_U_M_Q')")
	@ApiOperation(value = "Query GovMenu", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<GovMenu> selectByPrimaryKey(@PathVariable("id") String id) {
		GovMenu govMenu = govMenuService.selectByPrimaryKey(id);
		if (govMenu == null) {
			return new ResponseRestEntity<GovMenu>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<GovMenu>(govMenu, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_GOV_U_M_E')")
	@ApiOperation(value = "Add GovMenu", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createGovMenu(@Valid @RequestBody GovMenu govMenu, BindingResult result, UriComponentsBuilder ucBuilder) {
		// govMenu.setMenuId(System.currentTimeMillis()+"");
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		GovMenu bean = govMenuService.selectByPrimaryKey(govMenu.getMenuId());
		if (govMenuService.isGovExist(govMenu) || bean != null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.create.conflict.message"));
		}

		govMenuService.insert(govMenu);
		//新增日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, govMenu,CommonLogImpl.GOV_USER);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/govMenu/{id}").buildAndExpand(govMenu.getMenuId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_GOV_U_M_E')")
	@ApiOperation(value = "Edit GovMenu", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<GovMenu> updateGovMenu(@PathVariable("id") String id, @Valid @RequestBody GovMenu govMenu, BindingResult result) {

		GovMenu currentGovMenu = govMenuService.selectByPrimaryKey(id);

		if (currentGovMenu == null) {
			return new ResponseRestEntity<GovMenu>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentGovMenu.setMenuKeyWord(govMenu.getMenuKeyWord());
		currentGovMenu.setMenuSortno(govMenu.getMenuSortno());
		currentGovMenu.setMenuNameCn(govMenu.getMenuNameCn());
		currentGovMenu.setMenuNameEn(govMenu.getMenuNameEn());
		currentGovMenu.setMenuNameEs(govMenu.getMenuNameEs());
		currentGovMenu.setMenuPic(govMenu.getMenuPic());
		currentGovMenu.setMenuType(govMenu.getMenuType());
		currentGovMenu.setPreMenuId(govMenu.getPreMenuId());
		currentGovMenu.setMenuProces(govMenu.getMenuProces());
		currentGovMenu.setRemark(govMenu.getRemark());

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<GovMenu>(currentGovMenu, HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}
		govMenuService.updateByPrimaryKey(currentGovMenu);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentGovMenu,CommonLogImpl.GOV_USER);
		return new ResponseRestEntity<GovMenu>(currentGovMenu, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_GOV_U_M_E')")
	@ApiOperation(value = "Edit Part GovMenu", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<GovMenu> updateGovMenuSelective(@PathVariable("id") String id, @RequestBody GovMenu govMenu) {

		GovMenu currentGovMenu = govMenuService.selectByPrimaryKey(id);

		if (currentGovMenu == null) {
			return new ResponseRestEntity<GovMenu>(HttpRestStatus.NOT_FOUND);
		}
		currentGovMenu.setMenuId(id);

		currentGovMenu.setMenuKeyWord(govMenu.getMenuKeyWord());
		currentGovMenu.setMenuSortno(govMenu.getMenuSortno());
		currentGovMenu.setMenuNameCn(govMenu.getMenuNameCn());
		currentGovMenu.setMenuNameEn(govMenu.getMenuNameEn());
		currentGovMenu.setMenuNameEs(govMenu.getMenuNameEs());
		currentGovMenu.setMenuPic(govMenu.getMenuPic());
		currentGovMenu.setMenuType(govMenu.getMenuType());
		currentGovMenu.setPreMenuId(govMenu.getPreMenuId());
		currentGovMenu.setMenuProces(govMenu.getMenuProces());
		currentGovMenu.setRemark(govMenu.getRemark());
		//修改日志
		govMenuService.updateByPrimaryKeySelective(currentGovMenu);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentGovMenu,CommonLogImpl.GOV_USER);
		return new ResponseRestEntity<GovMenu>(currentGovMenu, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_GOV_U_M_E')")
	@ApiOperation(value = "Delete GovMenu", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<GovMenu> deleteGovMenu(@PathVariable("id") String id) {

		GovMenu govMenu = govMenuService.selectByPrimaryKey(id);
		if (govMenu == null) {
			return new ResponseRestEntity<GovMenu>(HttpRestStatus.NOT_FOUND);
		}
		govMenuService.deleteByPrimaryKey(id);
		//删除日志开始
		GovMenu gov = new GovMenu();
				gov.setMenuId(id);
		    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, gov,CommonLogImpl.GOV_USER);
			//删除日志结束
		return new ResponseRestEntity<GovMenu>(HttpRestStatus.NO_CONTENT);
	}
	@ApiOperation(value = "search all gov menus", notes = "")
	@RequestMapping(value = "/userMenus/{userName}", method = RequestMethod.GET)
	public ResponseEntity<List<GovMenuUserMenuResponse>> getUserMenus(@PathVariable("userName") String userName, @RequestParam(required = false) String weburl) {
		GovUser govUser = govUserService.selectByUserName(userName);
		if (govUser == null) {
			return new ResponseEntity<List<GovMenuUserMenuResponse>>(HttpStatus.NOT_FOUND);
		}
		SysMenuUserMenuParam sysMenuUserMenuParam = new SysMenuUserMenuParam();
		sysMenuUserMenuParam.setUserId(govUser.getUserId());
		sysMenuUserMenuParam.setMenuType(MessageDef.MENU_TYPE.MENU);
		List<GovMenuUserMenuResponse> govMenuUserMenuResponseList = govMenuService.getUserMenus(sysMenuUserMenuParam, weburl);
		if (govMenuUserMenuResponseList == null || govMenuUserMenuResponseList.size() == 0) {
			return new ResponseEntity<List<GovMenuUserMenuResponse>>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<List<GovMenuUserMenuResponse>>(govMenuUserMenuResponseList, HttpStatus.OK);
		}
	}

}