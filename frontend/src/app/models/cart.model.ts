import { Product } from './product.model';

export interface CartItem {
  product: Product;
  quantity: number;
}

export interface FreightQuote {
  freightAmount: number;
  estimatedDeliveryDays: number;
  freeShipping: boolean;
  carrierName: string;
}

export interface CheckoutRequestPayload {
  customerId?: number;
  customerName: string;
  customerEmail: string;
  customerCpf: string;
  customerPhone: string;
  items: { productId: number; quantity: number }[];
  paymentMethod: 'PIX' | 'CREDIT_CARD' | 'BOLETO';
  couponCode?: string;
  fittCoinsToRedeem?: number;
  sellerCode?: string;
  deliveryZipCode: string;
  deliveryAddress: string;
}

export interface CheckoutResult {
  orderNumber: string;
  status: string;
  subtotal: number;
  discountAmount: number;
  freightAmount: number;
  totalAmount: number;
  paymentMethod: string;
  reservationUuid: string;
  reservationExpiresAt: string;
  pixQrCodeBase64?: string;
  pixCopiaECola?: string;
  fittCoinsEarned: number;
  message: string;
}

export interface OrderItemData {
  id?: number;
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export interface OrderData {
  id: number;
  orderNumber: string;
  customerId: number;
  status: string;
  subtotal: number;
  discountAmount: number;
  freightAmount: number;
  totalAmount: number;
  paymentMethod: string;
  trackingCode?: string;
  deliveryAddress?: string;
  deliveryZipCode?: string;
  fittCoinsEarned?: number;
  fittCoinsRedeemed?: number;
  createdAt: string;
  items?: OrderItemData[];
}
