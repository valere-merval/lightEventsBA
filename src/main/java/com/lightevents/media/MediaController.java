package com.lightevents.media;
import com.lightevents.shared.ApiException; import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*; import org.springframework.web.multipart.MultipartFile; import java.util.Map;
@RestController @RequestMapping("/api/media")
public class MediaController { private final MediaService media; public MediaController(MediaService media){this.media=media;}
 @PostMapping("/upload") public MediaService.MediaUpload upload(@RequestParam("file") MultipartFile file, @RequestParam(defaultValue="events") String folder) throws Exception { try { return media.upload(file, folder); } catch(Exception e){ throw new ApiException(HttpStatus.BAD_GATEWAY, "Upload S3 impossible: "+e.getMessage()); } }
 @PostMapping("/ai-image") public Map<String,Object> aiImage(@RequestBody Map<String,String> req){ try { MediaService.MediaUpload generated = media.generateEventCover(req.get("title"), req.get("description")); if(generated==null) return Map.of("status","OPENAI_OR_S3_NOT_CONFIGURED","imageUrl","","message","Configure OPENAI_API_KEY + S3_BUCKET/S3_ACCESS_KEY/S3_SECRET_KEY pour générer et stocker l'image."); return Map.of("status","GENERATED","imageUrl",generated.publicUrl(),"key",generated.key()); } catch(Exception e){ throw new ApiException(HttpStatus.BAD_GATEWAY, "Génération image OpenAI/S3 impossible: "+e.getMessage()); } }
}
