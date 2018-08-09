package com.zbensoft.e.payment.api.control;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;

@RestController
public class UploadTestController {
	
	private static final Logger log = LoggerFactory.getLogger(UploadTestController.class);

	// Save the uploaded file to this folder
	private static String UPLOADED_FOLDER = "d:\\tmp\\imgs\\";
	private static String DEST_FOLDER = "./target/classes/static/imgs/";
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	
	// @RequestMapping(value="/picture",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	// public Object add(@RequestBody String vo){
	//
	//
	// return null;
	//
	// }

	@PostMapping("/upload/picture") // //new annotation since 4.3
	public ResponseRestEntity<Picture> singleFileUpload(@RequestParam("avatar_src") Object src,
			@RequestParam("avatar_data") String data, @RequestParam("avatar_file") MultipartFile file,
			RedirectAttributes redirectAttributes) {

		HttpHeaders headers = new HttpHeaders();
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
			
			return new ResponseRestEntity<Picture>(headers, HttpRestStatus.BAD_REQUEST, localeMessageSourceService.getMessage("common.upload.file"));
		}

		try {
			//{"x":182.46153846153845,"y":90.61538461538463,"height":1116,"width":1116,"rotate":0}
			int x=0;
			int y=0;
			int w=0;
			int h=0;
			String[] datas=data.split(",");
			if(datas!=null&&datas.length==5){
				x=Integer.valueOf(datas[0].substring(datas[0].indexOf(":")+1,datas[0].indexOf(".")));
				y=Integer.valueOf(datas[1].substring(datas[1].indexOf(":")+1,datas[1].indexOf(".")));
				w=Integer.valueOf(datas[2].substring(datas[2].indexOf(":")+1,datas[2].indexOf(".")));
				h=Integer.valueOf(datas[3].substring(datas[3].indexOf(":")+1,datas[3].indexOf(".")));
			}
			
			
			
			// Get the file and save it somewhere
			byte[] bytes = file.getBytes();

			File dir = new File(UPLOADED_FOLDER);
			if (!dir.exists()) {
				dir.mkdir();
			}
			Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
			Files.write(path, bytes);

			cutImage(new File(UPLOADED_FOLDER + file.getOriginalFilename()),
					DEST_FOLDER + file.getOriginalFilename(), x,y,w,h);

			redirectAttributes.addFlashAttribute("message",
					"You successfully uploaded '" + file.getOriginalFilename() + "'");

		} catch (Exception e) {
			log.error("",e);
		}
		Picture pic =new Picture();
		pic.setPicName(file.getOriginalFilename());

		return new ResponseRestEntity<Picture>(pic,headers, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.upload.success"));
	}

	@GetMapping("/upload")
	public String index() {
		return "upload";
	}

	private static void cutImage(File src, String dest, int x, int y, int w, int h) throws IOException {
		Iterator iterator = ImageIO.getImageReadersByFormatName("jpg");
		ImageReader reader = (ImageReader) iterator.next();
		InputStream in = new FileInputStream(src);
		ImageInputStream iis = ImageIO.createImageInputStream(in);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		Rectangle rect = new Rectangle(x, y, w, h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		File destFile=new File(dest);
		
		ImageIO.write(bi, "jpg", destFile);

	}
	
	
	class Picture{
		private String picName;

		public String getPicName() {
			return picName;
		}

		public void setPicName(String picName) {
			this.picName = picName;
		}
		

		
	}



}