SELECT *
FROM getfunction.passkm_estimate_for_otd(
        ${trainId},
        ${trainThread},
        ${trainDepartureDate},
        ${depStation},
        ${arrStation}
     )