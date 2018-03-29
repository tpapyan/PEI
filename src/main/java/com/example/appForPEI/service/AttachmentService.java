package com.example.appForPEI.service;

import java.io.IOException;
import java.util.List;

import com.example.appForPEI.entity.Attachment;

public interface AttachmentService {
	
	List<Attachment> listAttachments();
	
	List<Attachment> listAttachmentsByUser();
	
	Attachment getUserByName(String username);

    void addAttachment(Attachment userInfo) throws IOException;

    void removeAttachment(Integer id);

    void updateAttachment(Attachment userInfo);

}
