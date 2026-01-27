package com.example.lo_linking_park.model;

public class Parking {
    private Ubication ubication;
    private String nom;
    private int limit,current,time_limit;

    public class Ubication {
        private String latitude,longitude;

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
    }
}