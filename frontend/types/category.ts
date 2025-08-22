// /types/category.ts
export interface Category {
    id: number;
    name: string;
}

export interface CreateCategoryInput {
    name: string;
}

export interface UpdateCategoryInput {
    name: string;
}
