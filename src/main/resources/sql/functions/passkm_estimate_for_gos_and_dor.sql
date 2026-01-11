SELECT *
FROM getfunction.passkm_estimate_for_gos_and_dor(
        ${trainId},
        ${trainThread},
        ${trainDepartureDate},
        2,
        ${depStation},
        ${arrStation}
     )