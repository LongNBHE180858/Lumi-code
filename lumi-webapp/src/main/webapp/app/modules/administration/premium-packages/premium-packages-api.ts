import axios from 'axios';

export type PremiumPackageType = 'PARENT_PERSONAL' | 'PARENT_FAMILY' | 'INSTRUCTOR_PERSONAL' | 'INSTRUCTOR_CLASS';

export interface PremiumPackage {
  id: number;
  name: string;
  packageType: PremiumPackageType | null;
  price: number;
  durationDays: number;
  storageQuotaMb: number;
  aiGenerationLimit: number;
  maxClasses: number | null;
  coveredPeople: number | null;
  revision: number;
  available: boolean;
  needsClassification: boolean;
}

export interface PremiumPackageInput {
  name: string;
  packageType: PremiumPackageType;
  price: number;
  durationDays: number;
  storageQuotaMb: number;
  aiGenerationLimit: number;
  maxClasses: number | null;
}

const url = 'api/admin/premium-packages';

export const premiumPackagesApi = {
  list: (page: number, size: number) => axios.get<PremiumPackage[]>(url, { params: { page, size, sort: 'id,desc' } }),
  create: (input: PremiumPackageInput) => axios.post<PremiumPackage>(url, input),
  update: (id: number, input: PremiumPackageInput) => axios.put<PremiumPackage>(`${url}/${id}`, input),
  setAvailability: (id: number, available: boolean) => axios.patch<PremiumPackage>(`${url}/${id}/availability`, { available }),
};
