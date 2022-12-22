/*
Airport ID, Identifier for this airport.
Name, Name of airport.
City, Main city served by airport.
Country, Country or territory where airport is located.
IATA, 3-letter IATA code. Null if not assigned/unknown.
ICAO, 4-letter ICAO code. Null if not assigned.
Latitude, Decimal degrees, usually to six significant digits.
Longitude, Decimal degrees, usually to six significant digits.
Altitude, In feet.
Timezone, Hours offset from UTC.
DST, Daylight savings time. One of E (Europe), A (US/Canada), S (South America), O (Australia), Z (New Zealand), N (None) or U (Unknown).
Tz database time zone, Timezone in "tz" (Olson) format, eg. "America/Los_Angeles".
Type, Type of the airport.
Source, Source of this data.
*/
package me.dusan.sfr.domains;

import java.time.ZoneId;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

//@Data
@AllArgsConstructor
@NoArgsConstructor
public class Airport implements Comparable<Airport>{
    private Long airportId;
    private String name;
    private String city;
    private String country;
    private String iata;
    private String icao;
    private double latitude;
    private double longitude;
    private double altitude;
    private /*ZoneOffset*/ double timezone;
    private char dst;
    private ZoneId tz;
    private String type;
    private String source;
    private Set<Airport> destinations;

    public Set<Airport> getDestinations() {
        return destinations;
    }

    public void setDestinations(Set<Airport> destinations) {
        this.destinations = destinations;
    }

    public Long getAirportId() {
        return airportId;
    }

    public void setAirportId(Long airportId) {
        this.airportId = airportId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getTimezone() {
        return timezone;
    }

    public void setTimezone(double timezone) {
        this.timezone = timezone;
    }

    public char getDst() {
        return dst;
    }

    public void setDst(char dst) {
        this.dst = dst;
    }

    public ZoneId getTz() {
        return tz;
    }

    public void setTz(ZoneId tz) {
        this.tz = tz;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "airportId=" + airportId +
               ", name='" + name + '\'' +
               ", city='" + city + '\'' +
               ", country='" + country + '\'' +
                ", iata='" + iata + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public int compareTo(Airport otheAirport) {
        return iata.compareTo(otheAirport.iata);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((iata == null) ? 0 : iata.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Airport other = (Airport) obj;
        if (iata == null) {
            if (other.iata != null)
                return false;
        } else if (!iata.equals(other.iata))
            return false;
        return true;
    }

    

}
