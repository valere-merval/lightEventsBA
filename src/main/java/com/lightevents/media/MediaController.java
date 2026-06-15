package com.lightevents.media;
import org.springframework.web.bind.annotation.*; import org.springframework.web.multipart.MultipartFile; import java.util.Map;
@RestController @RequestMapping("/api/media")
public class MediaController { private final MediaService media; public MediaController(MediaService media){this.media=media;}
 @PostMapping("/upload") public MediaService.MediaUpload upload(@RequestParam("file") MultipartFile file, @RequestParam(defaultValue="events") String folder) throws Exception { return media.upload(file, folder); }
 @PostMapping("/ai-image") public Map<String,Object> aiImage(@RequestBody Map<String,String> req){ MediaService.MediaUpload generated = media.generateEventCover(req.get("title"), req.get("description")); if(generated==null) return Map.of("status","OPENAI_OR_S3_NOT_CONFIGURED","imageUrl",""); return Map.of("status","GENERATED","imageUrl",generated.publicUrl(),"key",generated.key()); }
}
