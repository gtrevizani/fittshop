export interface OrderItemModel {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export interface OrderModel {
  id: number;
  orderNumber: string;
  customerId: number;
  status: 'CRIADO' | 'AGUARDANDO_PAGAMENTO' | 'PAGO' | 'EM_SEPARACAO' | 'EXPEDIDO' | 'ENTREGUE' | 'EXPIRADO' | 'CANCELADO';
  subtotal: number;
  discountAmount: number;
  freightAmount: number;
  totalAmount: number;
  paymentMethod: string;
  trackingCode?: string;
  sellerCode?: string;
  deliveryZipCode: string;
  deliveryAddress: string;
  fittCoinsEarned: number;
  fittCoinsRedeemed: number;
  createdAt: string;
  paidAt?: string;
  items: OrderItemModel[];
}
