/*
* Copyright 2013 National Bank of Belgium
*
* Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
* by the European Commission - subsequent versions of the EUPL (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://ec.europa.eu/idabc/eupl
*
* Unless required by applicable law or agreed to in writing, software 
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and 
* limitations under the Licence.
*/

package ec.tstoolkit.arima.estimation;

import ec.tstoolkit.design.Development;
import ec.tstoolkit.eco.ILikelihood;
import java.util.Formatter;

/**
 * @author Jean Palate
 */
@Development(status = Development.Status.Alpha)
public class LikelihoodStatistics {

    public static LikelihoodStatistics create(ILikelihood ll, int nobs, int nparams, double adj) {
        LikelihoodStatistics stats = new LikelihoodStatistics();
        stats.observationsCount = nobs;
        stats.effectiveObservationsCount = stats.observationsCount;
        stats.logLikelihood = ll.getLogLikelihood();
        stats.estimatedParametersCount = nobs - ll.getResiduals().length + nparams + 1;
        if (Double.isNaN(adj)) {
            adj = 0;
        }
        stats.transformationAdjustment = adj;
        stats.adjustedLogLikelihood = adj == 0 ? stats.logLikelihood : stats.logLikelihood + stats.transformationAdjustment;
        stats.SsqErr = ll.getSsqErr();
        stats.calc();
        return stats;
    }
    /**
     *
     */
    /**
     *
     */
    public int observationsCount, effectiveObservationsCount, estimatedParametersCount;
    /**
     *
     */
    /**
     *
     */
    /**
     *
     */
    /**
     *
     */
    public double logLikelihood, transformationAdjustment,
            adjustedLogLikelihood, AIC, AICC, BIC, BICC, BIC2, HannanQuinn;
    /**
     *
     */
    public double SsqErr;

    /**
     *
     */
    public void calc() {
        double n = effectiveObservationsCount;
        double np = estimatedParametersCount;
        double ll = adjustedLogLikelihood;
        double nll = logLikelihood;
        AIC = -2 * (ll - np);
        HannanQuinn = -2 * (ll - np * Math.log(Math.log(n)));
        AICC = -2 * (ll - (n * np) / (n - np - 1));
        BIC = -2 * ll + np * Math.log(n);
        BIC2 = (-2 * nll + np * Math.log(n)) / n;
        BICC = Math.log(SsqErr / n) + (np - 1) * Math.log(n) / n;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Number of observations :").append(observationsCount).append("\r\n");
        builder.append("Effective number of observations :").append(
                effectiveObservationsCount).append("\r\n");
        builder.append("Number of parameters estimated :").append(
                estimatedParametersCount).append("\r\n");
        Formatter fmt = new Formatter();
        builder.append("log likelihood :").append(
                fmt.format("%.4f", logLikelihood)).append("\r\n");
        if (!Double.isNaN(transformationAdjustment)) {
            fmt = new Formatter();
            builder.append("Transformation Adjustment :").append(
                    fmt.format("%.4f", transformationAdjustment)).append("\r\n");
            fmt = new Formatter();
            builder.append("Adjusted log likelihood :").append(
                    fmt.format("%.4f", adjustedLogLikelihood)).append("\r\n");
        }
        fmt = new Formatter();
        builder.append("AIC :").append(fmt.format("%.4f", AIC)).append("\r\n");
        fmt = new Formatter();
//        builder.append("AICC (F-corrected-AIC) :").append(
//                fmt.format("%.4f", AICC)).append("\r\n");
//        fmt = new Formatter();
//        builder.append("Hannan Quinn :").append(fmt.format("%.4f", HannanQuinn)).append("\r\n");
//        fmt = new Formatter();
//        builder.append("BIC :").append(fmt.format("%.4f", BIC)).append("\r\n");
//        fmt = new Formatter();
        builder.append("BIC corrected for length :").append(fmt.format("%.4f", BICC));
        return builder.toString();

    }
}
