 @RequestMapping(value = "/getexecutedData" , method= RequestMethod.GET, produces =
            {"application/json"})
    public JSONObject getexecutedData(@RequestParam String fdate, @RequestParam String tdate, @RequestParam  String activity, @RequestParam String direction)
    {
        var query="select * from incident__master a join activity b on a.opsid= b.id";
        if(!activity.isEmpty())
        {
            query+= " where b.name = '"+activity+"'";
        }
        else if(!direction.isEmpty())
        {
            query+= " and a.directions = '"+direction+"'";
        }
        else if(!fdate.isEmpty() && !tdate.isEmpty()){
        {
             query+=" and a.incident_datetime between '"+fdate+ " 00:00:00.000' and '" + tdate+" 00:00:00.000'";
        }
    }
    //    var query="select * from incident__master a join activity b on a.opsid= b.id  where b.name = '"+activity+"' and a.incident_datetime between '"+fdate+ " 00:00:00.000' and '" + tdate+" 00:00:00.000'";
//        if(fdate!=null)
//        {
//            query+="'"+fdate+ " 00:00:00.000'";
//        }
//        if(tdate!=null)
//        {
//            query+="'"+tdate+ " 00:00:00.000'";
//        }
        ArrayList<incidentsDataObject> incidentsDataObject = new ArrayList<incidentsDataObject>();
        incidentsDataObject = (ArrayList<incidentsDataObject>) jdbcTemplate.query(query,new incidentsDataMapper());


        JSONObject featureCollection = new JSONObject();
        try {

            featureCollection.put("type","featureCollection");
            JSONArray featureList = new JSONArray();
            System.out.println("528>"+query);
            for (incidentsDataObject nav : incidentsDataObject) {

                System.out.println("531>");
                JSONObject point = new JSONObject();
                point.put("type","Point");
                JSONObject prop = new JSONObject();


                int lon = nav.getLongitude();
                System.out.println("52-->" + lon);
                int lat = nav.getLatitude();
                System.out.println("54-->" + lat);

                List<Integer> coord = new ArrayList<Integer>();
                coord.add(lon);
                coord.add(lat);
                point.put("coordinates",coord);
                JSONObject feature = new JSONObject();
                feature.put("type","Feature");
                feature.put("id","incident_"+nav.getIncident_id());


                prop.put("cases",nav.getCases_reported());
                prop.put("uid",nav.getIncident_id());
                prop.put("name",nav.getEntity());


                feature.put("geometry",point);
                feature.put("geometry_name","geometry");
                feature.put("properties",prop);


                featureList.add(feature);
                featureCollection.put("features",featureList);


            }
        }
        catch (Exception e)
        {
            System.out.println("Exception occured->"+e);

        }
        System.out.println("feature collection-->"+featureCollection.toString());


        return featureCollection;

    }
}