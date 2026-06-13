package com.lightevents.media;
import org.springframework.web.bind.annotation.*; import java.time.Instant; import java.util.Map; import java.util.UUID;
@RestController @RequestMapping("/api/media")
public class MediaController { public record UploadRequest(String fileName, String contentType, String folder) {}
 @PostMapping("/presign") public Map<String,Object> presign(@RequestBody UploadRequest r){ String key=(r.folder()==null?"events":r.folder())+"/"+UUID.randomUUID()+"-"+r.fileName(); return Map.of("provider","S3-compatible","method","PUT","uploadUrl","https://s3.example.com/lightevents/"+key+"?signature=configure-real-s3", "publicUrl","https://cdn.lightevents.africa/"+key, "expiresAt", Instant.now().plusSeconds(900).toString()); }
 @PostMapping("/ai-image") public Map<String,Object> aiImage(@RequestBody Map<String,String> req){ return Map.of("status","READY_TO_CONNECT_IMAGE_PROVIDER","prompt",req.getOrDefault("prompt","premium African event poster"),"imageUrl","https://images.unsplash.com/photo-1540575467063-178a50c2df87?auto=format&fit=crop&w=1600&q=80"); }
}
