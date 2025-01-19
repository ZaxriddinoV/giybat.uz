package giybat.uz.attach.service;

import giybat.uz.attach.dto.AttachDTO;
import giybat.uz.attach.dto.DovnloadDTO;
import giybat.uz.attach.dto.GetAttachDTO;
import giybat.uz.attach.entity.AttachEntity;
import giybat.uz.attach.repository.AttachRepository;
import giybat.uz.exceptionHandler.AppBadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class AttachService {

    private final String folderName = "attaches";
    private final String url = "/api/attach/download/";
    @Value("${server.domain}")
    private String domainName;
    @Autowired
    private AttachRepository attachRepository;

    public AttachDTO upload(MultipartFile file) {
        String pathFolder = getYmDString();
        String key = UUID.randomUUID().toString();
        String extension = getExtension(file.getOriginalFilename()); // .jpg, .png, .mp4
        File folder = new File(folderName + "/" + pathFolder); // attaches/2024/09/27
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(folderName + "/" + pathFolder + "/" + key + "." + extension);
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AttachEntity entity = new AttachEntity();
        entity.setId(key + "." + extension);
        entity.setPath(pathFolder);
        entity.setSize(file.getSize());
        entity.setOrigenName(file.getOriginalFilename());
        entity.setExtension(extension);
        entity.setVisible(true);
        attachRepository.save(entity);

        return toDTO(entity);
    }

    private String getYmDString() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DATE);
        return year + "/" + month + "/" + day;
    }

    private String getExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        return fileName.substring(lastIndex + 1);
    }

    private AttachDTO toDTO(AttachEntity entity) {
        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(entity.getId());
        attachDTO.setOriginName(entity.getOrigenName());
        attachDTO.setSize(entity.getSize());
        attachDTO.setExtension(entity.getExtension());
        attachDTO.setCreatedData(entity.getCreatedDate());
        attachDTO.setUrl(url + entity.getId());
        return attachDTO;
    }


    public ResponseEntity<Resource> open(String id) {
        AttachEntity entity = getEntity(id);
        String path = folderName + "/" + entity.getPath() + "/" + entity.getId();
        Path filePath = Paths.get(path).normalize();
        Resource resource = null;
        try {
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new RuntimeException("File not found: " + path);
            }
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public AttachEntity getEntity(String id) {
        Optional<AttachEntity> optional = attachRepository.findById(id);
        if (optional.isEmpty()) {
            throw new AppBadException("File not found");
        }
        return optional.get();
    }

    public Page<AttachEntity> allAttaches(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<AttachEntity> entityList = attachRepository.allAttach(pageRequest);
        Long total = entityList.getTotalElements();
        List<AttachDTO> dtoList = new LinkedList<>();
        for (AttachEntity entity : entityList) {
            dtoList.add(toDTO(entity));
        }
        PageImpl page1 = new PageImpl<>(dtoList, pageRequest, total);

        return page1;
    }
    public GetAttachDTO getUrl(String id) {
        AttachEntity entity = getEntity(id);
        GetAttachDTO dto = new GetAttachDTO();
        dto.setId(entity.getId());
        dto.setUrl(domainName + "/open/name/" + entity.getId());
        return dto;
    }


    public DovnloadDTO download(String fileName) {
        try {
            AttachEntity entity = getEntity(fileName);
            String path = folderName + "/" + entity.getPath() + "/" + entity.getId();
            Path file = Paths.get(path);
            List<DovnloadDTO> dtoList = new LinkedList<>();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                DovnloadDTO dovnloadDTO = new DovnloadDTO();
                dovnloadDTO.setName(entity.getOrigenName());
                dovnloadDTO.setResource(resource);
                return dovnloadDTO;
            } else {
                throw new AppBadException("File not found");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AttachDTO getDTO(String attachId) {
        return null;
    }
}

