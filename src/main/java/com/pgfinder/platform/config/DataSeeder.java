package com.pgfinder.platform.config;

import com.pgfinder.platform.domain.Amenity;
import com.pgfinder.platform.domain.GenderType;
import com.pgfinder.platform.domain.Listing;
import com.pgfinder.platform.domain.Review;
import com.pgfinder.platform.domain.RoomOption;
import com.pgfinder.platform.domain.RoomType;
import com.pgfinder.platform.repository.ListingRepository;
import com.pgfinder.platform.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ListingRepository listingRepository;
    private final ReviewRepository reviewRepository;

    public DataSeeder(ListingRepository listingRepository, ReviewRepository reviewRepository) {
        this.listingRepository = listingRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<Listing> listings = new ArrayList<>();

        listings.add(buildListing(
            "urban-nest-gomti-nagar",
            "Urban Nest Co-Living",
            "Gomti Nagar",
            "Lucknow",
            "Viraj Khand 4, Gomti Nagar, Lucknow",
            "Premium co-living with chef meals and cowork-ready spaces.",
            "Urban Nest is designed for students and young professionals who want hotel-grade cleanliness, secure entry, and quick access to IT hubs in Gomti Nagar.",
            "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=1200&q=80",
            List.of(
                "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1484154218962-a197022b5858?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=1200&q=80"
            ),
            6200,
            12900,
            6200,
            GenderType.COED,
            true,
            true,
            true,
            true,
            "UrbanNest",
            24,
            4.6,
            42,
            "Indira Nagar Metro",
            "Near Cyber Heights",
            26.8557,
            81.0160,
            "Priyansh Singh",
            "+91 98711 22110",
            EnumSet.of(Amenity.WIFI, Amenity.AC, Amenity.FOOD, Amenity.POWER_BACKUP, Amenity.CCTV, Amenity.BIOMETRIC_ENTRY, Amenity.HOUSEKEEPING, Amenity.LAUNDRY, Amenity.STUDY_TABLE)
        ));

        listings.add(buildListing(
            "kings-boys-hostel-indira-nagar",
            "Kings Boys Hostel",
            "Indira Nagar",
            "Lucknow",
            "Sector 11, Indira Nagar, Lucknow",
            "Affordable boys PG with AC and daily housekeeping.",
            "Kings Boys Hostel offers practical budgets with no hidden costs, unlimited WiFi, and strong metro connectivity for college students.",
            "https://images.unsplash.com/photo-1523217582562-09d0def993a6?auto=format&fit=crop&w=1200&q=80",
            List.of(
                "https://images.unsplash.com/photo-1505692952047-1a78307da8f2?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1616594039964-3f6fb0a1f782?auto=format&fit=crop&w=1200&q=80"
            ),
            3800,
            8400,
            3000,
            GenderType.BOYS,
            false,
            true,
            true,
            false,
            null,
            16,
            4.3,
            21,
            "Munshi Pulia Metro",
            "Near Polytechnic Chauraha",
            26.8839,
            80.9994,
            "Rahul Mishra",
            "+91 93100 45122",
            EnumSet.of(Amenity.WIFI, Amenity.AC, Amenity.POWER_BACKUP, Amenity.CCTV, Amenity.HOUSEKEEPING, Amenity.WATER_PURIFIER)
        ));

        listings.add(buildListing(
            "sunflower-girls-residency-aliganj",
            "Sunflower Girls Residency",
            "Aliganj",
            "Lucknow",
            "Sector B, Aliganj, Lucknow",
            "Safe girls-only residency with biometric entry and in-house meals.",
            "Sunflower Girls Residency is focused on safety and comfort with a women-led management team, 24x7 security, and healthy meal plans.",
            "https://images.unsplash.com/photo-1560185008-a33f4d9e6f33?auto=format&fit=crop&w=1200&q=80",
            List.of(
                "https://images.unsplash.com/photo-1493666438817-866a91353ca9?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1501183638710-841dd1904471?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1540518614846-7eded433c457?auto=format&fit=crop&w=1200&q=80"
            ),
            5500,
            11500,
            5000,
            GenderType.GIRLS,
            true,
            true,
            false,
            true,
            "StayBloom",
            12,
            4.8,
            58,
            "IT College Metro",
            "Near Kendriya Bhawan",
            26.8773,
            80.9557,
            "Aparna Srivastava",
            "+91 97952 10444",
            EnumSet.of(Amenity.WIFI, Amenity.AC, Amenity.FOOD, Amenity.CCTV, Amenity.BIOMETRIC_ENTRY, Amenity.HOUSEKEEPING, Amenity.HOT_WATER, Amenity.ATTACHED_WASHROOM)
        ));

        listings.add(buildListing(
            "comfy-corner-hazratganj",
            "Comfy Corner Executive Stay",
            "Hazratganj",
            "Lucknow",
            "Park Road, Hazratganj, Lucknow",
            "Executive PG with private rooms and premium services.",
            "Comfy Corner blends homely comfort with executive facilities, ideal for working professionals who want premium location and managed services.",
            "https://images.unsplash.com/photo-1616627561839-074385245ff6?auto=format&fit=crop&w=1200&q=80",
            List.of(
                "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1523755231516-e43fd2e8dca5?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1598928506311-c55ded91a20c?auto=format&fit=crop&w=1200&q=80"
            ),
            9000,
            16800,
            9000,
            GenderType.COED,
            true,
            true,
            false,
            false,
            null,
            8,
            4.4,
            14,
            "Sachivalaya Metro",
            "Near Janpath Market",
            26.8467,
            80.9462,
            "Karan Mehta",
            "+91 98871 23010",
            EnumSet.of(Amenity.WIFI, Amenity.AC, Amenity.FOOD, Amenity.POWER_BACKUP, Amenity.CCTV, Amenity.LIFT, Amenity.GYM, Amenity.HOUSEKEEPING, Amenity.TV)
        ));

        listings.add(buildListing(
            "scholar-hub-bbd-chinhat",
            "Scholar Hub PG",
            "Chinhat",
            "Lucknow",
            "Near BBD University, Chinhat, Lucknow",
            "Student-centric PG with budget plans and shared study zones.",
            "Scholar Hub is crafted for campus life with fast internet, dedicated study lounge, optional mess plans, and multiple sharing options.",
            "https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=1200&q=80",
            List.of(
                "https://images.unsplash.com/photo-1519710164239-da123dc03ef4?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1523217582562-09d0def993a6?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1578898887932-dce23a595ad4?auto=format&fit=crop&w=1200&q=80"
            ),
            3200,
            7600,
            2500,
            GenderType.COED,
            false,
            true,
            true,
            false,
            null,
            31,
            4.1,
            33,
            "Munshi Pulia Metro",
            "Near BBD College Gate",
            26.8747,
            81.0864,
            "Satyam Dubey",
            "+91 99111 20189",
            EnumSet.of(Amenity.WIFI, Amenity.POWER_BACKUP, Amenity.CCTV, Amenity.LAUNDRY, Amenity.STUDY_TABLE, Amenity.WATER_PURIFIER)
        ));

        listings.add(buildListing(
            "royal-girls-pg-ashiyana",
            "Royal Girls PG",
            "Ashiyana",
            "Lucknow",
            "Sector K, Ashiyana, Lucknow",
            "Girls PG with homely food and strict safety standards.",
            "Royal Girls PG offers modern rooms, supervised entry, and peaceful surroundings with easy access to transport and markets.",
            "https://images.unsplash.com/photo-1481277542470-605612bd2d61?auto=format&fit=crop&w=1200&q=80",
            List.of(
                "https://images.unsplash.com/photo-1505691938895-1758d7feb511?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1560448204-603b3fc33ddc?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1560448075-bb4caa6f6f18?auto=format&fit=crop&w=1200&q=80"
            ),
            4800,
            9800,
            4500,
            GenderType.GIRLS,
            true,
            true,
            false,
            true,
            "HerStay",
            19,
            4.7,
            27,
            "Krishna Nagar Metro",
            "Near Lulu Mall Road",
            26.7772,
            80.9080,
            "Nupur Awasthi",
            "+91 98070 77661",
            EnumSet.of(Amenity.WIFI, Amenity.AC, Amenity.FOOD, Amenity.CCTV, Amenity.BIOMETRIC_ENTRY, Amenity.HOUSEKEEPING, Amenity.HOT_WATER, Amenity.ATTACHED_WASHROOM, Amenity.LAUNDRY)
        ));

        listings.add(buildListing(
            "metroline-boys-pg-alambagh",
            "Metroline Boys PG",
            "Alambagh",
            "Lucknow",
            "Transport Nagar Road, Alambagh, Lucknow",
            "Value-for-money boys PG near metro and transport hubs.",
            "Metroline Boys PG is suitable for job seekers and students needing quick commute, clean rooms, and simple monthly packages.",
            "https://images.unsplash.com/photo-1512918728675-ed5a9ecdebfd?auto=format&fit=crop&w=1200&q=80",
            List.of(
                "https://images.unsplash.com/photo-1502672023488-70e25813eb80?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1599619585752-c3ed0f2a2e3b?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1631679706909-1844bbd07221?auto=format&fit=crop&w=1200&q=80"
            ),
            3500,
            7000,
            2500,
            GenderType.BOYS,
            false,
            true,
            false,
            false,
            null,
            28,
            4.0,
            18,
            "Alambagh Bus Stand Metro",
            "Near Transport Nagar",
            26.8012,
            80.8967,
            "Javed Ali",
            "+91 89532 21211",
            EnumSet.of(Amenity.WIFI, Amenity.POWER_BACKUP, Amenity.CCTV, Amenity.PARKING, Amenity.WATER_PURIFIER)
        ));

        listings.add(buildListing(
            "zen-living-vibhuti-khand",
            "Zen Living Luxe Suites",
            "Vibhuti Khand",
            "Lucknow",
            "Vibhuti Khand, Gomti Nagar, Lucknow",
            "Luxury managed PG suites for professionals and founders.",
            "Zen Living Luxe offers curated interiors, concierge support, gym access, lounge spaces, and business-grade internet for premium residents.",
            "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&w=1200&q=80",
            List.of(
                "https://images.unsplash.com/photo-1493666438817-866a91353ca9?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1460317442991-0ec209397118?auto=format&fit=crop&w=1200&q=80"
            ),
            11000,
            22000,
            15000,
            GenderType.COED,
            true,
            true,
            true,
            true,
            "ZenLiving",
            6,
            4.9,
            66,
            "Indira Nagar Metro",
            "Near Summit Building",
            26.8528,
            81.0088,
            "Aman Tewari",
            "+91 97190 98880",
            EnumSet.of(Amenity.WIFI, Amenity.AC, Amenity.FOOD, Amenity.POWER_BACKUP, Amenity.CCTV, Amenity.BIOMETRIC_ENTRY, Amenity.GYM, Amenity.LIFT, Amenity.INDOOR_GAMES, Amenity.HOUSEKEEPING, Amenity.TV)
        ));

        listings.add(buildListing(
            "study-stay-jankipuram",
            "StudyStay Jankipuram",
            "Jankipuram",
            "Lucknow",
            "Sector F, Jankipuram, Lucknow",
            "Quiet budget PG focused on exam prep and long stays.",
            "StudyStay is a no-distraction residence offering reading rooms, stable WiFi, and month-on-month plans for aspirants.",
            "https://images.unsplash.com/photo-1560185127-6ed189bf02f4?auto=format&fit=crop&w=1200&q=80",
            List.of(
                "https://images.unsplash.com/photo-1493666438817-866a91353ca9?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1505691938895-1758d7feb511?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1505692952047-1a78307da8f2?auto=format&fit=crop&w=1200&q=80"
            ),
            3000,
            6200,
            2000,
            GenderType.COED,
            false,
            true,
            false,
            false,
            null,
            34,
            4.2,
            30,
            "IT College Metro",
            "Near Kursi Road Junction",
            26.9221,
            80.9425,
            "Vikas Shukla",
            "+91 90445 00311",
            EnumSet.of(Amenity.WIFI, Amenity.POWER_BACKUP, Amenity.STUDY_TABLE, Amenity.WATER_PURIFIER, Amenity.LAUNDRY)
        ));

        listings.add(buildListing(
            "heritage-home-stay-charbagh",
            "Heritage Home Stay",
            "Charbagh",
            "Lucknow",
            "Naka Hindola, Charbagh, Lucknow",
            "Traditional home-style PG with meal plans and metro access.",
            "Heritage Home Stay is family-run and popular for fresh food, strict cleanliness, and easy walking distance to Charbagh station.",
            "https://images.unsplash.com/photo-1560185007-cde436f6a4d0?auto=format&fit=crop&w=1200&q=80",
            List.of(
                "https://images.unsplash.com/photo-1502005097973-6a7082348e28?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1616486029423-aaa4789e8c9a?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1523755231516-e43fd2e8dca5?auto=format&fit=crop&w=1200&q=80"
            ),
            4200,
            9200,
            3000,
            GenderType.COED,
            true,
            true,
            false,
            false,
            null,
            20,
            4.5,
            25,
            "Charbagh Metro",
            "Near Charbagh Railway Station",
            26.8312,
            80.9205,
            "Shalini Verma",
            "+91 90057 33114",
            EnumSet.of(Amenity.WIFI, Amenity.FOOD, Amenity.CCTV, Amenity.HOUSEKEEPING, Amenity.HOT_WATER, Amenity.WATER_PURIFIER)
        ));

        listings.add(buildListing(
            "skyline-suites-koramangala",
            "Skyline Suites Co-Living",
            "Koramangala",
            "Bangalore",
            "5th Block, Koramangala, Bangalore",
            "Premium co-living suites for startup professionals and students.",
            "Skyline Suites offers managed rooms, cowork corners, high-speed WiFi, and wellness amenities with easy access to offices and cafes.",
            "https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=1200&q=80",
            List.of(
                "https://images.unsplash.com/photo-1519710164239-da123dc03ef4?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1493666438817-866a91353ca9?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=1200&q=80"
            ),
            8500,
            16500,
            9000,
            GenderType.COED,
            true,
            true,
            true,
            false,
            "Skyline",
            18,
            4.7,
            47,
            "Indiranagar Metro",
            "Near Forum Mall",
            12.9346,
            77.6117,
            "Naveen Rao",
            "+91 98040 11108",
            EnumSet.of(Amenity.WIFI, Amenity.AC, Amenity.FOOD, Amenity.CCTV, Amenity.LIFT, Amenity.GYM, Amenity.HOUSEKEEPING, Amenity.LAUNDRY, Amenity.BIOMETRIC_ENTRY)
        ));

        listings.add(buildListing(
            "marine-boys-pg-andheri-east",
            "Marine Boys PG",
            "Andheri East",
            "Mumbai",
            "Marol, Andheri East, Mumbai",
            "Metro-connected boys PG with flexible budgets.",
            "Marine Boys PG is designed for working bachelors with practical plans, dependable security, and quick transit options.",
            "https://images.unsplash.com/photo-1502672023488-70e25813eb80?auto=format&fit=crop&w=1200&q=80",
            List.of(
                "https://images.unsplash.com/photo-1599619585752-c3ed0f2a2e3b?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1631679706909-1844bbd07221?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1512918728675-ed5a9ecdebfd?auto=format&fit=crop&w=1200&q=80"
            ),
            6000,
            11800,
            7000,
            GenderType.BOYS,
            false,
            true,
            false,
            false,
            null,
            22,
            4.3,
            31,
            "Andheri Metro",
            "Near Marol Naka",
            19.1136,
            72.8697,
            "Irfan Shaikh",
            "+91 98921 00354",
            EnumSet.of(Amenity.WIFI, Amenity.AC, Amenity.CCTV, Amenity.POWER_BACKUP, Amenity.LAUNDRY, Amenity.HOUSEKEEPING, Amenity.WATER_PURIFIER)
        ));

        listings.add(buildListing(
            "saket-girls-residency-delhi",
            "Saket Girls Residency",
            "Saket",
            "Delhi",
            "Saket Block D, New Delhi",
            "Secure girls residency with biometric access and meal plans.",
            "Saket Girls Residency provides safe, modern rooms with housekeeping and quick metro reach for students and professionals.",
            "https://images.unsplash.com/photo-1481277542470-605612bd2d61?auto=format&fit=crop&w=1200&q=80",
            List.of(
                "https://images.unsplash.com/photo-1505691938895-1758d7feb511?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1560448204-603b3fc33ddc?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1560448075-bb4caa6f6f18?auto=format&fit=crop&w=1200&q=80"
            ),
            7600,
            14900,
            8000,
            GenderType.GIRLS,
            true,
            true,
            true,
            false,
            "StayBloom",
            14,
            4.6,
            39,
            "Malviya Nagar Metro",
            "Near Select Citywalk",
            28.5245,
            77.2066,
            "Pooja Batra",
            "+91 98733 22019",
            EnumSet.of(Amenity.WIFI, Amenity.AC, Amenity.FOOD, Amenity.CCTV, Amenity.BIOMETRIC_ENTRY, Amenity.HOUSEKEEPING, Amenity.HOT_WATER, Amenity.ATTACHED_WASHROOM)
        ));

        listings.add(buildListing(
            "hitech-nest-madhapur",
            "HiTech Nest PG",
            "Madhapur",
            "Hyderabad",
            "Ayyappa Society, Madhapur, Hyderabad",
            "Tech-corridor PG with premium internet and managed services.",
            "HiTech Nest is built for IT professionals with quiet rooms, cowork seating, and rapid access to Hitec City offices.",
            "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&w=1200&q=80",
            List.of(
                "https://images.unsplash.com/photo-1460317442991-0ec209397118?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1598928506311-c55ded91a20c?auto=format&fit=crop&w=1200&q=80"
            ),
            7200,
            14200,
            7000,
            GenderType.COED,
            true,
            true,
            false,
            true,
            "TechStay",
            20,
            4.5,
            36,
            "HITEC City Metro",
            "Near Cyber Towers",
            17.4474,
            78.3762,
            "Sandeep Reddy",
            "+91 94911 74022",
            EnumSet.of(Amenity.WIFI, Amenity.AC, Amenity.FOOD, Amenity.CCTV, Amenity.POWER_BACKUP, Amenity.GYM, Amenity.HOUSEKEEPING, Amenity.LAUNDRY, Amenity.LIFT)
        ));

        listings.add(buildListing(
            "pcmc-student-hub-wakad",
            "PCMC Student Hub",
            "Wakad",
            "Pune",
            "Datta Mandir Road, Wakad, Pune",
            "Budget-friendly student PG with exam-focused study spaces.",
            "PCMC Student Hub is ideal for college students looking for affordable rent, stable WiFi, and calm study zones.",
            "https://images.unsplash.com/photo-1560185127-6ed189bf02f4?auto=format&fit=crop&w=1200&q=80",
            List.of(
                "https://images.unsplash.com/photo-1493666438817-866a91353ca9?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1505692952047-1a78307da8f2?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1502005097973-6a7082348e28?auto=format&fit=crop&w=1200&q=80"
            ),
            5000,
            9800,
            4500,
            GenderType.COED,
            false,
            true,
            false,
            false,
            null,
            26,
            4.2,
            28,
            "PCMC Metro",
            "Near Wakad Chowk",
            18.5993,
            73.7649,
            "Mayur Jagtap",
            "+91 95520 11064",
            EnumSet.of(Amenity.WIFI, Amenity.POWER_BACKUP, Amenity.CCTV, Amenity.STUDY_TABLE, Amenity.LAUNDRY, Amenity.WATER_PURIFIER)
        ));

        listings.add(buildListing(
            "marina-living-adyar",
            "Marina Living Residency",
            "Adyar",
            "Chennai",
            "Lattice Bridge Road, Adyar, Chennai",
            "Comfort-first co-living near colleges and IT parks.",
            "Marina Living offers furnished rooms, hygienic meals, and consistent maintenance with convenient city access.",
            "https://images.unsplash.com/photo-1560185007-cde436f6a4d0?auto=format&fit=crop&w=1200&q=80",
            List.of(
                "https://images.unsplash.com/photo-1523755231516-e43fd2e8dca5?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1616486029423-aaa4789e8c9a?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=1200&q=80"
            ),
            6800,
            13600,
            6500,
            GenderType.COED,
            true,
            true,
            false,
            false,
            null,
            17,
            4.4,
            24,
            "Little Mount Metro",
            "Near IIT Madras Gate",
            13.0012,
            80.2565,
            "Shreya Narayan",
            "+91 94440 08821",
            EnumSet.of(Amenity.WIFI, Amenity.AC, Amenity.FOOD, Amenity.CCTV, Amenity.HOUSEKEEPING, Amenity.HOT_WATER, Amenity.ATTACHED_WASHROOM, Amenity.POWER_BACKUP)
        ));

        enrichRoomOptions(listings);
        List<Listing> listingsToInsert = listings.stream()
            .filter(listing -> !listingRepository.existsBySlug(listing.getSlug()))
            .toList();

        if (listingsToInsert.isEmpty()) {
            return;
        }

        List<Listing> saved = listingRepository.saveAll(listingsToInsert);
        seedReviews(saved);
    }

    private Listing buildListing(
        String slug,
        String title,
        String locality,
        String city,
        String address,
        String shortDescription,
        String description,
        String mainImageUrl,
        List<String> gallery,
        int startingPrice,
        int endingPrice,
        int securityDeposit,
        GenderType genderType,
        boolean foodIncluded,
        boolean verified,
        boolean partnerVerified,
        boolean brandNew,
        String brandName,
        int availableBeds,
        double rating,
        int reviewCount,
        String nearbyMetro,
        String nearbyLandmark,
        double latitude,
        double longitude,
        String contactName,
        String contactPhone,
        Set<Amenity> amenities
    ) {
        Listing listing = new Listing();
        listing.setSlug(slug);
        listing.setTitle(title);
        listing.setLocality(locality);
        listing.setCity(city);
        listing.setAddress(address);
        listing.setShortDescription(shortDescription);
        listing.setDescription(description);
        listing.setMainImageUrl(mainImageUrl);
        listing.setGalleryImages(new ArrayList<>(gallery));
        listing.setStartingPrice(startingPrice);
        listing.setEndingPrice(endingPrice);
        listing.setSecurityDeposit(securityDeposit);
        listing.setGenderType(genderType);
        listing.setFoodIncluded(foodIncluded);
        listing.setVerified(verified);
        listing.setPartnerVerified(partnerVerified);
        listing.setBrandNew(brandNew);
        listing.setBrandName(brandName);
        listing.setAvailableBeds(availableBeds);
        listing.setRatingAvg(rating);
        listing.setReviewCount(reviewCount);
        listing.setNearbyMetro(nearbyMetro);
        listing.setNearbyLandmark(nearbyLandmark);
        listing.setLatitude(latitude);
        listing.setLongitude(longitude);
        listing.setContactName(contactName);
        listing.setContactPhone(contactPhone);
        listing.setAmenities(EnumSet.copyOf(amenities));
        return listing;
    }

    private void enrichRoomOptions(List<Listing> listings) {
        for (Listing listing : listings) {
            listing.addRoomOption(room(RoomType.PRIVATE_ROOM, "Single Room", listing.getEndingPrice(), true, true, 3));
            listing.addRoomOption(room(RoomType.DOUBLE_SHARING, "Twin Sharing", Math.max(listing.getStartingPrice() + 800, listing.getStartingPrice()), false, true, 6));
            listing.addRoomOption(room(RoomType.TRIPLE_SHARING, "Triple Sharing", listing.getStartingPrice(), false, false, 12));

            if (listing.getStartingPrice() <= 6500) {
                listing.addRoomOption(room(RoomType.FOUR_PLUS_SHARING, "4+ Sharing", Math.max(2500, listing.getStartingPrice() - 900), false, false, 10));
            }
        }
    }

    private RoomOption room(RoomType roomType, String label, int price, boolean acIncluded, boolean attachedWashroom, int availableBeds) {
        RoomOption roomOption = new RoomOption();
        roomOption.setRoomType(roomType);
        roomOption.setLabel(label);
        roomOption.setPrice(price);
        roomOption.setAcIncluded(acIncluded);
        roomOption.setAttachedWashroom(attachedWashroom);
        roomOption.setAvailableBeds(availableBeds);
        return roomOption;
    }

    private void seedReviews(List<Listing> listings) {
        List<Review> reviews = new ArrayList<>();

        for (Listing listing : listings) {
            reviews.add(review(listing, "Aditi", "Great location and responsive staff.", listing.getRatingAvg()));
            reviews.add(review(listing, "Rohit", "Clean rooms and fast WiFi for work.", Math.max(3.8, listing.getRatingAvg() - 0.3)));
        }

        reviewRepository.saveAll(reviews);
    }

    private Review review(Listing listing, String reviewer, String comment, double rating) {
        Review review = new Review();
        review.setListing(listing);
        review.setReviewerName(reviewer);
        review.setOverallRating(rating);
        review.setLocationRating(rating);
        review.setStaffRating(rating);
        review.setFoodRating(rating);
        review.setCleanlinessRating(rating);
        review.setWifiRating(rating);
        review.setComment(comment);
        return review;
    }
}
