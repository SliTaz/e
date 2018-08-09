package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

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
import com.zbensoft.e.payment.api.service.api.SysMenuService;
import com.zbensoft.e.payment.api.service.api.SysUserService;
import com.zbensoft.e.payment.db.domain.SysMenu;
import com.zbensoft.e.payment.db.domain.SysMenuUserMenuParam;
import com.zbensoft.e.payment.db.domain.SysMenuUserMenuResponse;
import com.zbensoft.e.payment.db.domain.SysUser;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/menu")
@RestController
public class SysMenuController {
	@Autowired
	SysMenuService sysMenuService;

	@Autowired
	SysUserService sysUserService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_MANAG_U_M_Q')")
	@ApiOperation(value = "Query sysMenu,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<SysMenu>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String menuId, @RequestParam(required = false) String menuKeyWord,
			@RequestParam(required = false) Integer menuSortno, @RequestParam(required = false) String menuNameCn,
			@RequestParam(required = false) String menuNameEn,@RequestParam(required = false) String menuNameEs,
			@RequestParam(required = false) String menuPic, @RequestParam(required = false) String menuType,
			@RequestParam(required = false) String preMenuId, @RequestParam(required = false) String menuProces,
			@RequestParam(required = false) String remark, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		SysMenu menu = new SysMenu();
		menu.setMenuId(id);
		menu.setMenuKeyWord(menuKeyWord);
		menu.setMenuSortno(menuSortno);
		menu.setMenuNameCn(menuNameCn);
		menu.setMenuNameEn(menuNameEn);
		menu.setMenuNameEs(menuNameEs);
		menu.setMenuPic(menuPic);
		menu.setMenuType(menuType);
		menu.setPreMenuId(preMenuId);
		menu.setMenuProces(menuProces);
		menu.setRemark(remark);

		int count = sysMenuService.count(menu);
		if (count == 0) {
			return new ResponseRestEntity<List<SysMenu>>(new ArrayList<SysMenu>(), HttpRestStatus.NOT_FOUND);
		}

		List<SysMenu> list = null;// consumerUserService.selectPage(consumerUser);

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = sysMenuService.selectPage(menu);
		} else {
			list = sysMenuService.selectPage(menu);
		}

		return new ResponseRestEntity<List<SysMenu>>(list, HttpRestStatus.OK, count, count);
	}
	@PreAuthorize("hasRole('R_MANAG_U_M_Q')")
	@ApiOperation(value = "Query sysMenu", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<SysMenu> selectByPrimaryKey(@PathVariable("id") String id) {
		SysMenu sysMenu = sysMenuService.selectByPrimaryKey(id);
		if (sysMenu == null) {
			return new ResponseRestEntity<SysMenu>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<SysMenu>(sysMenu, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_MANAG_U_M_E')")
	@ApiOperation(value = "Add sysMenu", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createMenu(@RequestBody SysMenu menu,BindingResult result,  UriComponentsBuilder ucBuilder) {
		//menu.setMenuId(IDGenerate.generateSYS_USER_ID());
		// 校验
				if (result.hasErrors()) {
					List<ObjectError> list = result.getAllErrors();
					return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
							HttpRestStatusFactory.createStatusMessage(list));
				}
				// 是否存在相同用户名或者主键是否存在
				SysMenu bean = sysMenuService.selectByPrimaryKey(menu.getMenuId());
				if (sysMenuService.isRoleExist(menu) || bean !=null) {
					return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
				}
		sysMenuService.insert(menu);
		//新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, menu,CommonLogImpl.MANAGE_USER);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/menu/{id}").buildAndExpand(menu.getMenuId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_MANAG_U_M_E')")
	@ApiOperation(value = "Edit sysMenu", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<SysMenu> updateMenu(@PathVariable("id") String id, @RequestBody SysMenu menu) {

		SysMenu currentMenu = sysMenuService.selectByPrimaryKey(id);

		if (currentMenu == null) {
			return new ResponseRestEntity<SysMenu>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentMenu.setMenuId(menu.getMenuId());
		currentMenu.setMenuKeyWord(menu.getMenuKeyWord());
		currentMenu.setMenuSortno(menu.getMenuSortno());
		currentMenu.setMenuNameCn(menu.getMenuNameCn());
		currentMenu.setMenuNameEn(menu.getMenuNameEn());
		currentMenu.setMenuNameEs(menu.getMenuNameEs());
		currentMenu.setMenuPic(menu.getMenuPic());
		currentMenu.setMenuType(menu.getMenuType());
		currentMenu.setPreMenuId(menu.getPreMenuId());
		currentMenu.setMenuProces(menu.getMenuProces());
		currentMenu.setRemark(menu.getRemark());

		sysMenuService.updateByPrimaryKey(currentMenu);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentMenu,CommonLogImpl.MANAGE_USER);
		return new ResponseRestEntity<SysMenu>(currentMenu, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_MANAG_U_M_E')")
	@ApiOperation(value = "Edit Part sysMenu", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<SysMenu> updateMenuSelective(@PathVariable("id") String id, @RequestBody SysMenu menu) {

		SysMenu currentMenu = sysMenuService.selectByPrimaryKey(id);

		if (currentMenu == null) {
			return new ResponseRestEntity<SysMenu>(HttpRestStatus.NOT_FOUND);
		}
		sysMenuService.updateByPrimaryKeySelective(menu);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, menu,CommonLogImpl.MANAGE_USER);
		return new ResponseRestEntity<SysMenu>(currentMenu, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_MANAG_U_M_E')")
	@ApiOperation(value = "Delete sysMenu", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<SysMenu> deleteMenu(@PathVariable("id") String id) {

		SysMenu menu = sysMenuService.selectByPrimaryKey(id);
		if (menu == null) {
			return new ResponseRestEntity<SysMenu>(HttpRestStatus.NOT_FOUND);
		}

		sysMenuService.deleteByPrimaryKey(id);
		//删除日志开始
		SysMenu sys = new SysMenu();
						sys.setMenuId(id);
				    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, sys,CommonLogImpl.MANAGE_USER);
					//删除日志结束
		return new ResponseRestEntity<SysMenu>(HttpRestStatus.NO_CONTENT);
	}

	//@PreAuthorize("hasRole('R_MANAG_U_M_Q')")
	@ApiOperation(value = "Query all userMenus", notes = "")
	@RequestMapping(value = "/userMenus/{userName}", method = RequestMethod.GET)
	public ResponseEntity<List<SysMenuUserMenuResponse>> getUserMenus(@PathVariable("userName") String userName,
			@RequestParam(required = false) String weburl) {
		SysUser sysUser = sysUserService.selectByUserName(userName);
		if (sysUser == null) {
			return new ResponseEntity<List<SysMenuUserMenuResponse>>(HttpStatus.NOT_FOUND);
		}
		SysMenuUserMenuParam sysMenuUserMenuParam = new SysMenuUserMenuParam();
		sysMenuUserMenuParam.setUserId(sysUser.getUserId());
		sysMenuUserMenuParam.setMenuType(MessageDef.MENU_TYPE.MENU);
		List<SysMenuUserMenuResponse> sysMenuUserMenuResponseList = sysMenuService.getUserMenus(sysMenuUserMenuParam,
				weburl);
		if (sysMenuUserMenuResponseList == null || sysMenuUserMenuResponseList.size() == 0) {
			return new ResponseEntity<List<SysMenuUserMenuResponse>>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<List<SysMenuUserMenuResponse>>(sysMenuUserMenuResponseList, HttpStatus.OK);
		}
	}
}