package com.example.appForPEI.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.wiztools.xsdgen.ParseException;
import org.wiztools.xsdgen.XsdGen;
import org.xml.sax.InputSource;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;
import com.example.appForPEI.entity.UserInfo;
import com.example.appForPEI.exception.StorageException;
import com.example.appForPEI.properties.StorageProperties;
import com.example.appForPEI.service.StorageService;
import com.example.appForPEI.service.UserInfoService;
import com.example.appForPEI.exception.StorageFileNotFoundException;
import com.example.appForPEI.exception.XmlException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

@Service
public class StorageServiceImpl implements StorageService  {

	private static final Logger LOG = LoggerFactory.getLogger(StorageServiceImpl.class);

	private final Path uploadFolder;
	
	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	public StorageServiceImpl(StorageProperties properties) {
		this.uploadFolder = Paths.get(properties.getUploadFolder());
	}

	public Path load(String filename) {
		return uploadFolder.resolve(filename);
	}

	public File convert(MultipartFile file) {
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = null;
		try {
			convFile.createNewFile();
			fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return convFile;
	}

	public String formatXSD(String xsdString) {
		try {

			Source xmlInput = new StreamSource(new StringReader(xsdString));
			StringWriter stringWriter = new StringWriter();
			StreamResult xmlOutput = new StreamResult(stringWriter);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", 2);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(xmlInput, xmlOutput);

			return xmlOutput.getWriter().toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void store(MultipartFile file) {
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserInfo currentUser = userInfoService.getUserByName(userDetails.getUsername());
		
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		String fileNameXSD = currentUser.getId() + "_" + filename.replaceFirst("[.][^.]+$", "") + ".xsd";

		try {
			if (!(file.getContentType().contains("text/xml") || file.getContentType().contains("application/xml"))) {
				throw new XmlException("File must be xml");
			}

			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + filename);
			}

			if (filename.contains("..")) {
				throw new StorageException(
						"Cannot store file with relative path outside current directory " + filename);
			}

			File convFile = convert(file);
			String xsd;
			try {
				xsd = formatXSD(new XsdGen().parse(convFile).toString());
				System.out.println(xsd);
				uploadOrderFile(xsd, fileNameXSD);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} catch (IOException e) {
			throw new StorageException("Failed to store file " + filename, e);
		}
	}

	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.uploadFolder, 1).filter(path -> !path.equals(this.uploadFolder))
					.map(path -> this.uploadFolder.relativize(path));
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}
	}
	
	public Stream<Path> loadAllByUser(Integer userId) {
		try {
			return Files.walk(this.uploadFolder, 1).filter(path -> !path.equals(this.uploadFolder))
					.filter(path -> path.getFileName().toString().startsWith(userId + "_"))
					.map(path -> this.uploadFolder.relativize(path));
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}
	}

	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new StorageFileNotFoundException("Could not read file: " + filename);

			}
		} catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	public void deleteAll() {
		FileSystemUtils.deleteRecursively(uploadFolder.toFile());
	}

	public void init() {
		try {
			Files.createDirectories(uploadFolder);
		} catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}

	public boolean uploadOrderFile(String xsdStream, String filename) {
		try {
			byte[] bytes;
			bytes = xsdStream.getBytes();
			Path path = this.uploadFolder.resolve(filename);
			Files.write(path, bytes);
			return true;
		} catch (IOException e) {
			LOG.error("Error uploading a file {}", filename);
			return false;
		}
	}

}
