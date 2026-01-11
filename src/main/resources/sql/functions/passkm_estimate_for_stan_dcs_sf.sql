SELECT *
FROM getfunction.passkm_estimate_for_stan_dcs_sf(
        ${trainId},
        ${trainThread},
        ${trainDepartureDate},
        ${mode},
        ${depStation},
        ${arrStation}
     )