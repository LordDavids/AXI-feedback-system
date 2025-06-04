type Form = {
    id: number;
    name: string;
    isActive: boolean;
    questions: Question[];
    category: FormCategory;
    isPublic: boolean;
};