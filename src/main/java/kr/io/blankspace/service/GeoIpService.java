package kr.io.blankspace.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

@Service
@RequiredArgsConstructor
public class GeoIpService {
    private final DatabaseReader reader;

    public String resolveRegion(String ip) {
        try {
            InetAddress addr = InetAddress.getByName(ip);

            if (addr.isAnyLocalAddress() || addr.isLoopbackAddress() || addr.isSiteLocalAddress())
            { return "LOCAL"; }

            CityResponse res = reader.city(addr);

            String country = res.getCountry().getIsoCode();
            String city = res.getCity().getName();
            String subdivision = res.getMostSpecificSubdivision().getName();

            String c = country != null ? country : "UNKNOWN";
            String r = city != null && !city.isBlank() ? city : subdivision;
            if (r == null || r.isBlank()) r = "UNKNOWN";

            return c + ", " + r;
        } catch (Exception e) { return "UNKNOWN"; }
    }
}
