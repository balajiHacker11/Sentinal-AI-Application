package com.example.data.model

data class PoliceStation(
    val id: String,
    val name: String,
    val district: String,
    val address: String,
    val phone: String,
    val pincode: String,
    val latitude: Double,
    val longitude: Double,
    val jurisdiction: String
)

object PoliceStationProvider {
    val districts = listOf(
        "All Districts", "Ariyalur", "Chengalpattu", "Chennai", "Coimbatore",
        "Cuddalore", "Dharmapuri", "Dindigul", "Erode", "Kallakurichi",
        "Kanchipuram", "Kanyakumari", "Karur", "Krishnagiri", "Madurai",
        "Mayiladuthurai", "Nagapattinam", "Namakkal", "Nilgiris", "Perambalur",
        "Pudukkottai", "Ramanathapuram", "Ranipet", "Salem", "Sivaganga",
        "Tenkasi", "Thanjavur", "Theni", "Thoothukudi", "Tiruchirappalli",
        "Tirunelveli", "Tirupathur", "Tiruppur", "Tiruvallur", "Tiruvannamalai",
        "Tiruvarur", "Vellore", "Viluppuram", "Virudhunagar"
    )

    val tnPoliceStations = listOf(
        // CHENNAI
        PoliceStation(
            id = "awps_chennai_01",
            name = "AWPS Kilpauk (W-1)",
            district = "Chennai",
            address = "Ormes Road, Kilpauk, Chennai, Tamil Nadu",
            phone = "044-23452331",
            pincode = "600010",
            latitude = 13.0802,
            longitude = 80.2412,
            jurisdiction = "Kilpauk, Chetpet, Purasawalkam"
        ),
        PoliceStation(
            id = "awps_chennai_02",
            name = "AWPS Thousand Lights (W-2)",
            district = "Chennai",
            address = "Greams Road, Thousand Lights, Chennai, Tamil Nadu",
            phone = "044-23452332",
            pincode = "600006",
            latitude = 13.0601,
            longitude = 80.2520,
            jurisdiction = "Greams Road, Nungambakkam, Gopalapuram"
        ),
        PoliceStation(
            id = "awps_chennai_03",
            name = "AWPS Egmore (W-3)",
            district = "Chennai",
            address = "Pantheon Road, Egmore, Chennai, Tamil Nadu",
            phone = "044-23452333",
            pincode = "600008",
            latitude = 13.0732,
            longitude = 80.2611,
            jurisdiction = "Egmore Railway Area, Chintadripet"
        ),
        PoliceStation(
            id = "awps_chennai_04",
            name = "AWPS Royapettah (W-4)",
            district = "Chennai",
            address = "Whannels Road, Royapettah, Chennai, Tamil Nadu",
            phone = "044-23452334",
            pincode = "600014",
            latitude = 13.0521,
            longitude = 80.2625,
            jurisdiction = "Royapettah, Triplicane, Mylapore"
        ),
        PoliceStation(
            id = "awps_chennai_05",
            name = "AWPS Washermenpet (W-5)",
            district = "Chennai",
            address = "GA Road, Washermenpet, Chennai, Tamil Nadu",
            phone = "044-23452335",
            pincode = "600021",
            latitude = 13.1118,
            longitude = 80.2831,
            jurisdiction = "Washermenpet, Royapuram, Tondiarpet"
        ),
        PoliceStation(
            id = "awps_chennai_06",
            name = "AWPS Flower Bazaar (W-6)",
            district = "Chennai",
            address = "NSC Bose Road, Parrys, Chennai, Tamil Nadu",
            phone = "044-23452336",
            pincode = "600001",
            latitude = 13.0889,
            longitude = 80.2825,
            jurisdiction = "Parrys Corner, George Town, Elephant Gate"
        ),
        PoliceStation(
            id = "awps_chennai_07",
            name = "AWPS Pulianthope (W-7)",
            district = "Chennai",
            address = "Pulianthope High Road, Chennai, Tamil Nadu",
            phone = "044-23452337",
            pincode = "600012",
            latitude = 13.0975,
            longitude = 80.2633,
            jurisdiction = "Pulianthope, Otteri, Vepery"
        ),
        PoliceStation(
            id = "awps_chennai_08",
            name = "AWPS Adyar (W-8)",
            district = "Chennai",
            address = "Lattice Bridge Road, Adyar, Chennai, Tamil Nadu",
            phone = "044-23452338",
            pincode = "600020",
            latitude = 13.0012,
            longitude = 80.2565,
            jurisdiction = "Adyar, Besant Nagar, Thiruvanmiyur"
        ),
        PoliceStation(
            id = "awps_chennai_09",
            name = "AWPS Guindy (W-9)",
            district = "Chennai",
            address = "GST Road, Guindy, Chennai, Tamil Nadu",
            phone = "044-23452339",
            pincode = "600032",
            latitude = 13.0067,
            longitude = 80.2021,
            jurisdiction = "Guindy Industrial Estate, Saidapet, Ekkatuthangal"
        ),
        PoliceStation(
            id = "awps_chennai_10",
            name = "AWPS Ashok Nagar (W-11)",
            district = "Chennai",
            address = "1st Avenue, Ashok Nagar, Chennai, Tamil Nadu",
            phone = "044-23452341",
            pincode = "600083",
            latitude = 13.0360,
            longitude = 80.2120,
            jurisdiction = "Ashok Nagar, KK Nagar, Vadapalani"
        ),
        PoliceStation(
            id = "awps_chennai_11",
            name = "AWPS Ambattur (W-12)",
            district = "Chennai",
            address = "MTH Road, Ambattur OT, Chennai, Tamil Nadu",
            phone = "044-23452342",
            pincode = "600053",
            latitude = 13.1143,
            longitude = 80.1548,
            jurisdiction = "Ambattur OT, Industrial Estate, Mogappair"
        ),
        PoliceStation(
            id = "awps_chennai_12",
            name = "AWPS Avadi (W-13)",
            district = "Chennai",
            address = "NM Road, Avadi, Chennai, Tamil Nadu",
            phone = "044-23452343",
            pincode = "600054",
            latitude = 13.1189,
            longitude = 80.1012,
            jurisdiction = "Avadi, Pattabiram, HVF Colony"
        ),

        // CHENGALPATTU
        PoliceStation(
            id = "awps_chengalpattu_01",
            name = "AWPS Tambaram",
            district = "Chengalpattu",
            address = "GST Road, West Tambaram, Chennai, Tamil Nadu",
            phone = "044-22264445",
            pincode = "600045",
            latitude = 12.9249,
            longitude = 80.1168,
            jurisdiction = "Tambaram, Chromepet, Sanatorium"
        ),
        PoliceStation(
            id = "awps_chengalpattu_02",
            name = "AWPS Chengalpattu",
            district = "Chengalpattu",
            address = "GST Road, Chengalpattu Town, Tamil Nadu",
            phone = "044-27422100",
            pincode = "603001",
            latitude = 12.6841,
            longitude = 79.9836,
            jurisdiction = "Chengalpattu Town, Paranur, Singaperumal Koil"
        ),
        PoliceStation(
            id = "awps_chengalpattu_03",
            name = "AWPS Madurantakam",
            district = "Chengalpattu",
            address = "Car Street, Madurantakam, Tamil Nadu",
            phone = "044-27552100",
            pincode = "603306",
            latitude = 12.5089,
            longitude = 79.8856,
            jurisdiction = "Madurantakam, Melmaruvathur"
        ),

        // KANCHIPURAM
        PoliceStation(
            id = "awps_kanchi_01",
            name = "AWPS Kanchipuram Town",
            district = "Kanchipuram",
            address = "West Raja Street, Kanchipuram, Tamil Nadu",
            phone = "044-27222100",
            pincode = "631501",
            latitude = 12.8342,
            longitude = 79.7036,
            jurisdiction = "Kanchipuram Temple Zone, Bus Stand Area"
        ),
        PoliceStation(
            id = "awps_kanchi_02",
            name = "AWPS Sriperumbudur",
            district = "Kanchipuram",
            address = "Bangalore Highway, Sriperumbudur, Tamil Nadu",
            phone = "044-27162100",
            pincode = "602105",
            latitude = 12.9691,
            longitude = 79.9431,
            jurisdiction = "Sriperumbudur Industrial Hub, Sunguvarchatram"
        ),

        // TIRUVALLUR
        PoliceStation(
            id = "awps_tiruvallur_01",
            name = "AWPS Tiruvallur Town",
            district = "Tiruvallur",
            address = "JN Road, Tiruvallur, Tamil Nadu",
            phone = "044-27660100",
            pincode = "602001",
            latitude = 13.1432,
            longitude = 79.9080,
            jurisdiction = "Tiruvallur Town, Collectorate Area, Veppampattu"
        ),
        PoliceStation(
            id = "awps_tiruvallur_02",
            name = "AWPS Tiruttani",
            district = "Tiruvallur",
            address = "Arakkonam Road, Tiruttani, Tamil Nadu",
            phone = "044-27880100",
            pincode = "631209",
            latitude = 13.1782,
            longitude = 79.6321,
            jurisdiction = "Tiruttani Hill Temple Zone, Bus Stand"
        ),
        PoliceStation(
            id = "awps_tiruvallur_03",
            name = "AWPS Gummidipoondi",
            district = "Tiruvallur",
            address = "GNT Road, Gummidipoondi, Tamil Nadu",
            phone = "044-27922100",
            pincode = "601201",
            latitude = 13.4071,
            longitude = 80.1219,
            jurisdiction = "Gummidipoondi SIPCOT, SR Kandigai"
        ),

        // RANIPET
        PoliceStation(
            id = "awps_ranipet_01",
            name = "AWPS Ranipet",
            district = "Ranipet",
            address = "MBT Road, Ranipet, Tamil Nadu",
            phone = "04172-272100",
            pincode = "632401",
            latitude = 12.9281,
            longitude = 79.3331,
            jurisdiction = "Ranipet Industrial Area, Walajapet"
        ),
        PoliceStation(
            id = "awps_ranipet_02",
            name = "AWPS Arakkonam",
            district = "Ranipet",
            address = "Gandhi Road, Arakkonam, Tamil Nadu",
            phone = "04177-232100",
            pincode = "631001",
            latitude = 13.0782,
            longitude = 79.6691,
            jurisdiction = "Arakkonam Railway Junction, INS Rajali Zone"
        ),

        // TIRUPATHUR
        PoliceStation(
            id = "awps_tirupathur_01",
            name = "AWPS Tirupathur Town",
            district = "Tirupathur",
            address = "Collectorate Road, Tirupathur, Tamil Nadu",
            phone = "04179-220100",
            pincode = "635601",
            latitude = 12.4921,
            longitude = 78.5689,
            jurisdiction = "Tirupathur Town, Yelagiri Hills Junction"
        ),
        PoliceStation(
            id = "awps_tirupathur_02",
            name = "AWPS Vaniyambadi",
            district = "Tirupathur",
            address = "CL Road, Vaniyambadi, Tamil Nadu",
            phone = "04174-222100",
            pincode = "635751",
            latitude = 12.6832,
            longitude = 78.6189,
            jurisdiction = "Vaniyambadi Town, Ambur Sector"
        ),

        // VELLORE
        PoliceStation(
            id = "awps_vellore_01",
            name = "AWPS Vellore Town",
            district = "Vellore",
            address = "Infantry Road, Near Fort, Vellore, Tamil Nadu",
            phone = "0416-2220100",
            pincode = "632001",
            latitude = 12.9165,
            longitude = 79.1325,
            jurisdiction = "Vellore Fort, CMC Hospital Area, Katpadi Junction"
        ),
        PoliceStation(
            id = "awps_vellore_02",
            name = "AWPS Gudiyatham",
            district = "Vellore",
            address = "Santhapet, Gudiyatham, Tamil Nadu",
            phone = "04171-220100",
            pincode = "632602",
            latitude = 12.9451,
            longitude = 78.8712,
            jurisdiction = "Gudiyatham Town, Pernambut Border Area"
        ),

        // TIRUVANNAMALAI
        PoliceStation(
            id = "awps_tvm_01",
            name = "AWPS Tiruvannamalai Town",
            district = "Tiruvannamalai",
            address = "Car Street, Girivalam Road, Tiruvannamalai, Tamil Nadu",
            phone = "04175-222100",
            pincode = "606601",
            latitude = 12.2253,
            longitude = 79.0747,
            jurisdiction = "Annamalaiyar Temple Area, Girivalam Path, Bus Stand"
        ),
        PoliceStation(
            id = "awps_tvm_02",
            name = "AWPS Arani",
            district = "Tiruvannamalai",
            address = "Market Road, Arani, Tamil Nadu",
            phone = "04173-222100",
            pincode = "632301",
            latitude = 12.6689,
            longitude = 79.2841,
            jurisdiction = "Arani Silk City, Polur Road"
        ),

        // DHARMAPURI
        PoliceStation(
            id = "awps_dharmapuri_01",
            name = "AWPS Dharmapuri Town",
            district = "Dharmapuri",
            address = "Kandasamy Vathiyar Street, Dharmapuri, Tamil Nadu",
            phone = "04342-260100",
            pincode = "636701",
            latitude = 12.1357,
            longitude = 78.1582,
            jurisdiction = "Dharmapuri Town, Collectorate Area, Pennagaram Road"
        ),
        PoliceStation(
            id = "awps_dharmapuri_02",
            name = "AWPS Harur",
            district = "Dharmapuri",
            address = "Bypass Road, Harur, Tamil Nadu",
            phone = "04346-222100",
            pincode = "636903",
            latitude = 12.0612,
            longitude = 78.4981,
            jurisdiction = "Harur Town, Pappireddipatti"
        ),

        // KRISHNAGIRI
        PoliceStation(
            id = "awps_krishnagiri_01",
            name = "AWPS Krishnagiri Town",
            district = "Krishnagiri",
            address = "Rayakottah Road, Krishnagiri, Tamil Nadu",
            phone = "04343-232100",
            pincode = "635001",
            latitude = 12.5266,
            longitude = 78.2146,
            jurisdiction = "Krishnagiri Town, Old Bus Stand, Tollgate"
        ),
        PoliceStation(
            id = "awps_krishnagiri_02",
            name = "AWPS Hosur",
            district = "Krishnagiri",
            address = "Bagalur Road, Hosur, Tamil Nadu",
            phone = "04344-222100",
            pincode = "635109",
            latitude = 12.7409,
            longitude = 77.8253,
            jurisdiction = "Hosur Industrial Complex, Sipcot, Bus Stand"
        ),

        // SALEM
        PoliceStation(
            id = "awps_salem_01",
            name = "AWPS Salem Town",
            district = "Salem",
            address = "Bretts Road, Near Old Bus Stand, Salem, Tamil Nadu",
            phone = "0427-2210100",
            pincode = "636001",
            latitude = 11.6539,
            longitude = 78.1583,
            jurisdiction = "Salem Town, Fort, Shevapet"
        ),
        PoliceStation(
            id = "awps_salem_02",
            name = "AWPS Suramangalam (Salem West)",
            district = "Salem",
            address = "Junction Main Road, Suramangalam, Salem, Tamil Nadu",
            phone = "0427-2210101",
            pincode = "636005",
            latitude = 11.6781,
            longitude = 78.1189,
            jurisdiction = "Salem Junction Railway Station, Suramangalam"
        ),
        PoliceStation(
            id = "awps_salem_03",
            name = "AWPS Attur",
            district = "Salem",
            address = "Ranipet Street, Attur, Tamil Nadu",
            phone = "04282-240100",
            pincode = "636102",
            latitude = 11.5982,
            longitude = 78.5981,
            jurisdiction = "Attur Town, Narasingapuram, Thammampatti"
        ),

        // NAMAKKAL
        PoliceStation(
            id = "awps_namakkal_01",
            name = "AWPS Namakkal Town",
            district = "Namakkal",
            address = "Tiruchengode Road, Namakkal, Tamil Nadu",
            phone = "04286-220100",
            pincode = "637001",
            latitude = 11.2189,
            longitude = 78.1678,
            jurisdiction = "Namakkal Anjaneyar Temple Area, Bus Stand, Park"
        ),
        PoliceStation(
            id = "awps_namakkal_02",
            name = "AWPS Tiruchengodu",
            district = "Namakkal",
            address = "Sankari Road, Tiruchengodu, Tamil Nadu",
            phone = "04288-252100",
            pincode = "637211",
            latitude = 11.3789,
            longitude = 77.8923,
            jurisdiction = "Tiruchengodu Hill Temple Area, Velur Road"
        ),

        // ERODE
        PoliceStation(
            id = "awps_erode_01",
            name = "AWPS Erode Town",
            district = "Erode",
            address = "Brough Road, Erode, Tamil Nadu",
            phone = "0424-2250100",
            pincode = "638001",
            latitude = 11.3410,
            longitude = 77.7172,
            jurisdiction = "Erode Bus Stand, Railway Colony, Perundurai Road"
        ),
        PoliceStation(
            id = "awps_erode_02",
            name = "AWPS Gobichettipalayam",
            district = "Erode",
            address = "Kutchery Street, Gobi, Tamil Nadu",
            phone = "04285-222100",
            pincode = "638452",
            latitude = 11.4542,
            longitude = 77.4389,
            jurisdiction = "Gobi Town, Sathyamangalam Highway"
        ),

        // TIRUPPUR
        PoliceStation(
            id = "awps_tiruppur_01",
            name = "AWPS Tiruppur North",
            district = "Tiruppur",
            address = "Kumaran Road, Tiruppur, Tamil Nadu",
            phone = "0421-2200100",
            pincode = "641601",
            latitude = 11.1085,
            longitude = 77.3411,
            jurisdiction = "Tiruppur Railway Station, New Bus Stand, Apparel Park"
        ),
        PoliceStation(
            id = "awps_tiruppur_02",
            name = "AWPS Dharapuram",
            district = "Tiruppur",
            address = "Pollachi Road, Dharapuram, Tamil Nadu",
            phone = "04258-220100",
            pincode = "638656",
            latitude = 10.7381,
            longitude = 77.5219,
            jurisdiction = "Dharapuram Town, Kangeyam Road"
        ),

        // COIMBATORE
        PoliceStation(
            id = "awps_cbe_01",
            name = "AWPS Coimbatore Central",
            district = "Coimbatore",
            address = "Goods Shed Road, Near Railway Station, Coimbatore, Tamil Nadu",
            phone = "0422-2300055",
            pincode = "641018",
            latitude = 11.0018,
            longitude = 76.9629,
            jurisdiction = "Ukadam, Town Hall, Gandhipuram"
        ),
        PoliceStation(
            id = "awps_cbe_02",
            name = "AWPS Coimbatore East (Singanallur)",
            district = "Coimbatore",
            address = "Trichy Road, Singanallur, Coimbatore, Tamil Nadu",
            phone = "0422-2300056",
            pincode = "641005",
            latitude = 11.0010,
            longitude = 77.0255,
            jurisdiction = "Singanallur, Peelamedu, Hope College, TIDEL Park"
        ),
        PoliceStation(
            id = "awps_cbe_03",
            name = "AWPS Pollachi",
            district = "Coimbatore",
            address = "New Scheme Road, Pollachi, Tamil Nadu",
            phone = "04259-223344",
            pincode = "642001",
            latitude = 10.6581,
            longitude = 77.0083,
            jurisdiction = "Pollachi Town, Mahalingapuram, Anaimalai"
        ),
        PoliceStation(
            id = "awps_cbe_04",
            name = "AWPS Mettupalayam",
            district = "Coimbatore",
            address = "Ooty Main Road, Mettupalayam, Tamil Nadu",
            phone = "04254-222100",
            pincode = "641301",
            latitude = 11.3001,
            longitude = 76.9451,
            jurisdiction = "Mettupalayam Railway Station, Foothills"
        ),

        // NILGIRIS
        PoliceStation(
            id = "awps_nilgiris_01",
            name = "AWPS Udhagamandalam (Ooty)",
            district = "Nilgiris",
            address = "Commercial Road, Ooty, Tamil Nadu",
            phone = "0423-2442100",
            pincode = "643001",
            latitude = 11.4102,
            longitude = 76.6950,
            jurisdiction = "Ooty Lake Area, Botanical Garden, Charing Cross"
        ),
        PoliceStation(
            id = "awps_nilgiris_02",
            name = "AWPS Coonoor",
            district = "Nilgiris",
            address = "Mount Road, Coonoor, Tamil Nadu",
            phone = "0423-2230100",
            pincode = "643101",
            latitude = 11.3530,
            longitude = 76.7959,
            jurisdiction = "Coonoor RS, Sim's Park, Wellington"
        ),

        // KARUR
        PoliceStation(
            id = "awps_karur_01",
            name = "AWPS Karur Town",
            district = "Karur",
            address = "Kovai Road, Karur, Tamil Nadu",
            phone = "04324-260100",
            pincode = "639001",
            latitude = 10.9601,
            longitude = 78.0766,
            jurisdiction = "Karur Bus Stand, Textile Park, Pasupatheeswarar Zone"
        ),
        PoliceStation(
            id = "awps_karur_02",
            name = "AWPS Kulithalai",
            district = "Karur",
            address = "Trichy Road, Kulithalai, Tamil Nadu",
            phone = "04323-222100",
            pincode = "639104",
            latitude = 10.9381,
            longitude = 78.4121,
            jurisdiction = "Kulithalai Town, Cauvery River Bank"
        ),

        // TIRUCHIRAPPALLI (TRICHY)
        PoliceStation(
            id = "awps_trichy_01",
            name = "AWPS Trichy Fort",
            district = "Tiruchirappalli",
            address = "Near Main Guard Gate, Fort Area, Trichy, Tamil Nadu",
            phone = "0431-2704100",
            pincode = "620002",
            latitude = 10.8282,
            longitude = 78.6942,
            jurisdiction = "Fort Station, Chatram Bus Stand, Rockfort"
        ),
        PoliceStation(
            id = "awps_trichy_02",
            name = "AWPS Trichy Cantonment",
            district = "Tiruchirappalli",
            address = "Collector Office Road, Cantonment, Trichy, Tamil Nadu",
            phone = "0431-2704101",
            pincode = "620001",
            latitude = 10.8041,
            longitude = 78.6823,
            jurisdiction = "Cantonment, Central Bus Stand, KK Nagar"
        ),
        PoliceStation(
            id = "awps_trichy_03",
            name = "AWPS Lalgudi",
            district = "Tiruchirappalli",
            address = "Main Road, Lalgudi, Trichy, Tamil Nadu",
            phone = "0431-2540100",
            pincode = "621601",
            latitude = 10.8689,
            longitude = 78.8189,
            jurisdiction = "Lalgudi Town, Samayapuram Temple Highway"
        ),

        // PERAMBALUR
        PoliceStation(
            id = "awps_perambalur_01",
            name = "AWPS Perambalur Town",
            district = "Perambalur",
            address = "Elambalur Road, Perambalur, Tamil Nadu",
            phone = "04328-224100",
            pincode = "621212",
            latitude = 11.2332,
            longitude = 78.8821,
            jurisdiction = "Perambalur Town, Old Bus Stand, Collectorate Area"
        ),

        // ARIYALUR
        PoliceStation(
            id = "awps_ariyalur_01",
            name = "AWPS Ariyalur Town",
            district = "Ariyalur",
            address = "Trichy Main Road, Ariyalur, Tamil Nadu",
            phone = "04329-222100",
            pincode = "621704",
            latitude = 11.1401,
            longitude = 79.0782,
            jurisdiction = "Ariyalur Town, Cement Factory Zone"
        ),
        PoliceStation(
            id = "awps_ariyalur_02",
            name = "AWPS Jayankondam",
            district = "Ariyalur",
            address = "Chidambaram Road, Jayankondam, Tamil Nadu",
            phone = "04331-250100",
            pincode = "621802",
            latitude = 11.2189,
            longitude = 79.3481,
            jurisdiction = "Jayankondam Town, Gangaikonda Cholapuram Zone"
        ),

        // CUDDALORE
        PoliceStation(
            id = "awps_cuddalore_01",
            name = "AWPS Cuddalore Town",
            district = "Cuddalore",
            address = "Beach Road, Cuddalore OT, Tamil Nadu",
            phone = "04142-230100",
            pincode = "607001",
            latitude = 11.7480,
            longitude = 79.7714,
            jurisdiction = "Cuddalore OT, Port Area, Manjakuppam"
        ),
        PoliceStation(
            id = "awps_cuddalore_02",
            name = "AWPS Chidambaram",
            district = "Cuddalore",
            address = "S P Kovil Street, Chidambaram, Tamil Nadu",
            phone = "04144-222100",
            pincode = "608001",
            latitude = 11.3992,
            longitude = 79.6931,
            jurisdiction = "Nataraja Temple Zone, Annamalai University, Bus Stand"
        ),
        PoliceStation(
            id = "awps_cuddalore_03",
            name = "AWPS Vriddhachalam",
            district = "Cuddalore",
            address = "Cuddalore Road, Vriddhachalam, Tamil Nadu",
            phone = "04143-230100",
            pincode = "606001",
            latitude = 11.5189,
            longitude = 79.3289,
            jurisdiction = "Vriddhachalam Junction, Bus Stand"
        ),

        // VILUPPURAM
        PoliceStation(
            id = "awps_viluppuram_01",
            name = "AWPS Viluppuram Town",
            district = "Viluppuram",
            address = "Trichy Trunk Road, Viluppuram, Tamil Nadu",
            phone = "04146-222100",
            pincode = "605602",
            latitude = 11.9391,
            longitude = 79.4921,
            jurisdiction = "Viluppuram Junction, Bus Stand, Collectorate"
        ),
        PoliceStation(
            id = "awps_viluppuram_02",
            name = "AWPS Tindivanam",
            district = "Viluppuram",
            address = "GST Road, Tindivanam, Tamil Nadu",
            phone = "04147-222100",
            pincode = "604001",
            latitude = 12.2289,
            longitude = 79.6521,
            jurisdiction = "Tindivanam Highway Junction, Mailam Road"
        ),

        // KALLAKURICHI
        PoliceStation(
            id = "awps_kallakurichi_01",
            name = "AWPS Kallakurichi Town",
            district = "Kallakurichi",
            address = "Kachirapalayam Road, Kallakurichi, Tamil Nadu",
            phone = "04151-222100",
            pincode = "606202",
            latitude = 11.7381,
            longitude = 78.9612,
            jurisdiction = "Kallakurichi Town, Kalvarayan Hills Base"
        ),

        // THANJAVUR
        PoliceStation(
            id = "awps_tj_01",
            name = "AWPS Thanjavur Town",
            district = "Thanjavur",
            address = "South Rampart, Near Big Temple, Thanjavur, Tamil Nadu",
            phone = "04362-230100",
            pincode = "613001",
            latitude = 10.7870,
            longitude = 79.1378,
            jurisdiction = "Thanjavur Old Bus Stand, Big Temple, Palace"
        ),
        PoliceStation(
            id = "awps_tj_02",
            name = "AWPS Kumbakonam",
            district = "Thanjavur",
            address = "Dr. Besant Road, Kumbakonam, Tamil Nadu",
            phone = "0435-2430100",
            pincode = "612001",
            latitude = 10.9617,
            longitude = 79.3881,
            jurisdiction = "Kumbakonam Town, Mahamaham Tank, Railway Area"
        ),
        PoliceStation(
            id = "awps_tj_03",
            name = "AWPS Pattukkottai",
            district = "Thanjavur",
            address = "Aramanthai Street, Pattukkottai, Tamil Nadu",
            phone = "04373-222100",
            pincode = "614601",
            latitude = 10.4289,
            longitude = 79.3189,
            jurisdiction = "Pattukkottai Bus Stand, Peravurani Road"
        ),

        // TIRUVARUR
        PoliceStation(
            id = "awps_tiruvarur_01",
            name = "AWPS Tiruvarur Town",
            district = "Tiruvarur",
            address = "Netaji Road, Tiruvarur, Tamil Nadu",
            phone = "04366-222100",
            pincode = "610001",
            latitude = 10.7721,
            longitude = 79.6361,
            jurisdiction = "Thyagaraja Swamy Temple Area, Railway Station"
        ),
        PoliceStation(
            id = "awps_tiruvarur_02",
            name = "AWPS Mannargudi",
            district = "Tiruvarur",
            address = "Pandaladi Street, Mannargudi, Tamil Nadu",
            phone = "04367-222100",
            pincode = "614001",
            latitude = 10.6681,
            longitude = 79.4489,
            jurisdiction = "Rajagopalaswamy Temple Zone, Bus Stand"
        ),

        // NAGAPATTINAM
        PoliceStation(
            id = "awps_nagapattinam_01",
            name = "AWPS Nagapattinam Town",
            district = "Nagapattinam",
            address = "Public Office Road, Nagapattinam, Tamil Nadu",
            phone = "04365-222100",
            pincode = "611001",
            latitude = 10.7651,
            longitude = 79.8421,
            jurisdiction = "Nagapattinam Port, Velankanni Church Highway"
        ),

        // MAYILADUTHURAI
        PoliceStation(
            id = "awps_mayiladuthurai_01",
            name = "AWPS Mayiladuthurai Town",
            district = "Mayiladuthurai",
            address = "Kacheri Road, Mayiladuthurai, Tamil Nadu",
            phone = "04364-222100",
            pincode = "609001",
            latitude = 11.1031,
            longitude = 79.6541,
            jurisdiction = "Mayiladuthurai Temple Zone, Bus Stand"
        ),

        // PUDUKKOTTAI
        PoliceStation(
            id = "awps_pudukkottai_01",
            name = "AWPS Pudukkottai Town",
            district = "Pudukkottai",
            address = "South Main Street, Pudukkottai, Tamil Nadu",
            phone = "04322-222100",
            pincode = "622001",
            latitude = 10.3831,
            longitude = 78.8211,
            jurisdiction = "Pudukkottai Town, Palace, Bus Stand"
        ),
        PoliceStation(
            id = "awps_pudukkottai_02",
            name = "AWPS Aranthangi",
            district = "Pudukkottai",
            address = "Fort Street, Aranthangi, Tamil Nadu",
            phone = "04371-222100",
            pincode = "614616",
            latitude = 10.1681,
            longitude = 78.9981,
            jurisdiction = "Aranthangi Town, Avudaiyarkoil Road"
        ),

        // DINDIGUL
        PoliceStation(
            id = "awps_dindigul_01",
            name = "AWPS Dindigul Town",
            district = "Dindigul",
            address = "Sub Collector Office Road, Dindigul, Tamil Nadu",
            phone = "0451-2422100",
            pincode = "624001",
            latitude = 10.3621,
            longitude = 77.9691,
            jurisdiction = "Rock Fort Area, Bus Stand, Collectorate"
        ),
        PoliceStation(
            id = "awps_dindigul_02",
            name = "AWPS Palani",
            district = "Dindigul",
            address = "Adivaram Road, Palani, Tamil Nadu",
            phone = "04545-242100",
            pincode = "624601",
            latitude = 10.4521,
            longitude = 77.5211,
            jurisdiction = "Palani Hill Temple Zone, Bus Stand, Railway Station"
        ),
        PoliceStation(
            id = "awps_dindigul_03",
            name = "AWPS Kodaikanal",
            district = "Dindigul",
            address = "Lake Road, Kodaikanal, Tamil Nadu",
            phone = "04542-240100",
            pincode = "624101",
            latitude = 10.2381,
            longitude = 77.4891,
            jurisdiction = "Kodaikanal Lake, Coaker's Walk, Bus Stand"
        ),

        // THENI
        PoliceStation(
            id = "awps_theni_01",
            name = "AWPS Theni Town",
            district = "Theni",
            address = "Periyakulam Road, Theni, Tamil Nadu",
            phone = "04546-252100",
            pincode = "625531",
            latitude = 10.0101,
            longitude = 77.4789,
            jurisdiction = "Theni New Bus Stand, Collectorate"
        ),
        PoliceStation(
            id = "awps_theni_02",
            name = "AWPS Periyakulam",
            district = "Theni",
            address = "Vadapalani Street, Periyakulam, Tamil Nadu",
            phone = "04546-232100",
            pincode = "625601",
            latitude = 10.1201,
            longitude = 77.5489,
            jurisdiction = "Periyakulam Town, Batlagundu Highway"
        ),

        // MADURAI
        PoliceStation(
            id = "awps_madurai_01",
            name = "AWPS Madurai South",
            district = "Madurai",
            address = "Periyar Bus Stand Area, Madurai Town, Tamil Nadu",
            phone = "0452-2345001",
            pincode = "625001",
            latitude = 9.9195,
            longitude = 78.1193,
            jurisdiction = "Madurai Town, Crime Branch South, Mahal Area"
        ),
        PoliceStation(
            id = "awps_madurai_02",
            name = "AWPS Madurai North (Tallakulam)",
            district = "Madurai",
            address = "Tallakulam Main Road, Madurai, Tamil Nadu",
            phone = "0452-2345002",
            pincode = "625002",
            latitude = 9.9324,
            longitude = 78.1345,
            jurisdiction = "Tallakulam, KK Nagar, Anna Nagar"
        ),
        PoliceStation(
            id = "awps_madurai_03",
            name = "AWPS Thiruparankundram",
            district = "Madurai",
            address = "GST Road, Thiruparankundram, Madurai, Tamil Nadu",
            phone = "0452-2345003",
            pincode = "625005",
            latitude = 9.8812,
            longitude = 78.0711,
            jurisdiction = "Thiruparankundram, Pasumalai, Austinpatti"
        ),
        PoliceStation(
            id = "awps_madurai_04",
            name = "AWPS Usilampatti",
            district = "Madurai",
            address = "Thevar Statue Circle, Usilampatti, Tamil Nadu",
            phone = "04543-252100",
            pincode = "625532",
            latitude = 9.9681,
            longitude = 77.7981,
            jurisdiction = "Usilampatti Town, Sedapatti Road"
        ),

        // VIRUDHUNAGAR
        PoliceStation(
            id = "awps_virudhunagar_01",
            name = "AWPS Virudhunagar Town",
            district = "Virudhunagar",
            address = "Madurai Road, Virudhunagar, Tamil Nadu",
            phone = "04562-243100",
            pincode = "626001",
            latitude = 9.5872,
            longitude = 77.9589,
            jurisdiction = "Virudhunagar Town, Collectorate, Railway Station"
        ),
        PoliceStation(
            id = "awps_virudhunagar_02",
            name = "AWPS Sivakasi",
            district = "Virudhunagar",
            address = "PKN Road, Sivakasi, Tamil Nadu",
            phone = "04562-220100",
            pincode = "626123",
            latitude = 9.4532,
            longitude = 77.7989,
            jurisdiction = "Sivakasi Cracker Industrial Hub, Town"
        ),
        PoliceStation(
            id = "awps_virudhunagar_03",
            name = "AWPS Rajapalayam",
            district = "Virudhunagar",
            address = "Tenkasi Road, Rajapalayam, Tamil Nadu",
            phone = "04563-220100",
            pincode = "626117",
            latitude = 9.4501,
            longitude = 77.5521,
            jurisdiction = "Rajapalayam Town, Ayyanar Falls Road"
        ),

        // SIVAGANGA
        PoliceStation(
            id = "awps_sivaganga_01",
            name = "AWPS Sivaganga Town",
            district = "Sivaganga",
            address = "Collectorate Complex Road, Sivaganga, Tamil Nadu",
            phone = "04575-240100",
            pincode = "630561",
            latitude = 9.8481,
            longitude = 78.4821,
            jurisdiction = "Sivaganga Palace, Bus Stand, District Court"
        ),
        PoliceStation(
            id = "awps_sivaganga_02",
            name = "AWPS Karaikudi",
            district = "Sivaganga",
            address = "100 Feet Road, Karaikudi, Tamil Nadu",
            phone = "04565-230100",
            pincode = "630001",
            latitude = 10.0701,
            longitude = 78.7821,
            jurisdiction = "Alagappa University, Chettinad Zone, Bus Stand"
        ),

        // RAMANATHAPURAM
        PoliceStation(
            id = "awps_ramnad_01",
            name = "AWPS Ramanathapuram Town",
            district = "Ramanathapuram",
            address = "Palace Road, Ramanathapuram, Tamil Nadu",
            phone = "04567-220100",
            pincode = "623501",
            latitude = 9.3701,
            longitude = 78.8321,
            jurisdiction = "Ramnad Palace, Collectorate, Railway Area"
        ),
        PoliceStation(
            id = "awps_ramnad_02",
            name = "AWPS Rameswaram",
            district = "Ramanathapuram",
            address = "Agneetheertham Road, Rameswaram, Tamil Nadu",
            phone = "04573-221100",
            pincode = "623526",
            latitude = 9.2881,
            longitude = 79.3121,
            jurisdiction = "Ramanathaswamy Temple Zone, Dhanushkodi Road"
        ),

        // TIRUNELVELI
        PoliceStation(
            id = "awps_tvl_01",
            name = "AWPS Tirunelveli Town",
            district = "Tirunelveli",
            address = "Swamy Nellaiappar High Road, Tirunelveli Town, Tamil Nadu",
            phone = "0462-2330100",
            pincode = "627006",
            latitude = 8.7282,
            longitude = 77.6891,
            jurisdiction = "Nellaiappar Temple Area, Pettai"
        ),
        PoliceStation(
            id = "awps_tvl_02",
            name = "AWPS Palayamkottai",
            district = "Tirunelveli",
            address = "High Ground Road, Palayamkottai, Tirunelveli, Tamil Nadu",
            phone = "0462-2330101",
            pincode = "627002",
            latitude = 8.7132,
            longitude = 77.7311,
            jurisdiction = "Palayamkottai, Medical College Hospital, NGO Colony"
        ),

        // TENKASI
        PoliceStation(
            id = "awps_tenkasi_01",
            name = "AWPS Tenkasi Town",
            district = "Tenkasi",
            address = "Courtallam Road, Tenkasi, Tamil Nadu",
            phone = "04633-222100",
            pincode = "627811",
            latitude = 8.9582,
            longitude = 77.3121,
            jurisdiction = "Tenkasi Kasi Viswanathar Temple Area, Courtallam"
        ),
        PoliceStation(
            id = "awps_tenkasi_02",
            name = "AWPS Sankarankovil",
            district = "Tenkasi",
            address = "Car Street, Sankarankovil, Tamil Nadu",
            phone = "04636-222100",
            pincode = "627756",
            latitude = 9.1721,
            longitude = 77.5321,
            jurisdiction = "Sankaranarayana Swamy Temple Area, Bus Stand"
        ),

        // THOOTHUKUDI (TUTICORIN)
        PoliceStation(
            id = "awps_thoothukudi_01",
            name = "AWPS Thoothukudi Town",
            district = "Thoothukudi",
            address = "Beach Road, Thoothukudi, Tamil Nadu",
            phone = "0461-2320100",
            pincode = "628001",
            latitude = 8.8001,
            longitude = 78.1489,
            jurisdiction = "Thoothukudi Port, Old Bus Stand, Thermal Zone"
        ),
        PoliceStation(
            id = "awps_thoothukudi_02",
            name = "AWPS Kovilpatti",
            district = "Thoothukudi",
            address = "Main Road, Kovilpatti, Tamil Nadu",
            phone = "04632-220100",
            pincode = "628501",
            latitude = 9.1782,
            longitude = 77.8689,
            jurisdiction = "Kovilpatti Town, Railway Station, Match Industry Zone"
        ),
        PoliceStation(
            id = "awps_thoothukudi_03",
            name = "AWPS Tiruchendur",
            district = "Thoothukudi",
            address = "Temple Road, Tiruchendur, Tamil Nadu",
            phone = "04639-242100",
            pincode = "628215",
            latitude = 8.4981,
            longitude = 78.1289,
            jurisdiction = "Subramaniya Swamy Temple Zone, Beach, Bus Stand"
        ),

        // KANYAKUMARI
        PoliceStation(
            id = "awps_kk_01",
            name = "AWPS Nagercoil (Kanyakumari)",
            district = "Kanyakumari",
            address = "Court Road, Nagercoil, Tamil Nadu",
            phone = "04652-230100",
            pincode = "629001",
            latitude = 8.1833,
            longitude = 77.4119,
            jurisdiction = "Nagercoil Town, Meenakshipuram, Vadasery"
        ),
        PoliceStation(
            id = "awps_kk_02",
            name = "AWPS Thuckalay",
            district = "Kanyakumari",
            address = "Padmanabhapuram Palace Road, Thuckalay, Tamil Nadu",
            phone = "04651-250100",
            pincode = "629175",
            latitude = 8.2481,
            longitude = 77.3289,
            jurisdiction = "Padmanabhapuram Palace, Thuckalay Bus Stand"
        ),
        PoliceStation(
            id = "awps_kk_03",
            name = "AWPS Marthandam",
            district = "Kanyakumari",
            address = "Trivandrum Highway, Marthandam, Tamil Nadu",
            phone = "04651-270100",
            pincode = "629165",
            latitude = 8.3001,
            longitude = 77.2289,
            jurisdiction = "Marthandam Flyover Area, Kuzhithurai"
        )
    )
}
