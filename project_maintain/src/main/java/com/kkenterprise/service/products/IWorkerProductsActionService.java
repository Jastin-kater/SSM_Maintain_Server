package com.kkenterprise.service.products;

import com.kkenterprise.domain.beans.Ordersbean;
import com.kkenterprise.domain.beans.WorkerOrderbean;

public interface IWorkerProductsActionService {



    WorkerOrderbean createWorkerOrder(Ordersbean order, Integer workerId);
}
