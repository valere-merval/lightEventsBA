package com.lightevents.geo;

import org.springframework.web.bind.annotation.*; import org.springframework.web.client.RestClient; import java.util.*;
@RestController @RequestMapping("/api/geo")
public class GeoController {
 private final RestClient rest = RestClient.builder().defaultHeader("User-Agent","LightEvents/1.0").build();
 public record AddressSuggestion(String label,String addressLine,String city,String postalCode,String state,String country,String countryCode,Double latitude,Double longitude){}
 @GetMapping("/address-suggest") public List<AddressSuggestion> suggest(@RequestParam String q){ if(q==null||q.isBlank()||q.length()<3) return List.of(); try{ List list=rest.get().uri("https://nominatim.openstreetmap.org/search?format=jsonv2&addressdetails=1&limit=6&q={q}", q).retrieve().body(List.class); return (List<AddressSuggestion>)list.stream().map(o->{ Map m=(Map)o; Map a=(Map)m.getOrDefault("address", Map.of()); String road=join(a.get("house_number"), a.get("road")); String city=first(a,"city","town","village","municipality"); return new AddressSuggestion(String.valueOf(m.get("display_name")), road, city, str(a.get("postcode")), str(a.get("state")), str(a.get("country")), str(a.get("country_code")).toUpperCase(), dbl(m.get("lat")), dbl(m.get("lon"))); }).toList(); }catch(Exception e){ return List.of(); }}
 private static String str(Object o){return o==null?"":String.valueOf(o);} private static Double dbl(Object o){try{return Double.valueOf(str(o));}catch(Exception e){return null;}} private static String join(Object a,Object b){return (str(a)+" "+str(b)).trim();} private static String first(Map a,String...ks){for(String k:ks) if(a.get(k)!=null) return str(a.get(k)); return "";}
}
