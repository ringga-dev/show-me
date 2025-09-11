// ========== JOIN TABLES (optional untuk dokumentasi) ==========
export interface UserPortfolioModel {
    id: string|null
    name: string|null
    image: string|null
    email: string|null
    phone: string|null
    address: string|null
    titles: string[]|null
    description: string|null
    hashTag: string[]|null
    socialMedia: UserPortfolioSocialModel[]|null
    workedWith: string[]|null
    aboutMe: string|null
    createdAt?: Date | null
    updatedAt?: Date | null
}

export interface UserPortfolioSocialModel {
    name: string|null
    url: string|null
    icon: string|null
}


export interface UserPortfolioSkillModel {
    id: string|null
    title: string|null
    icon: string|null
    description: string|null
    skillData: UserPortfolioSkillDataModel[]|null
    createdAt?: Date | null
    updatedAt?: Date | null
}

export interface UserPortfolioSkillDataModel {
    name: string|null
    percentage: number|null
}


export interface UserPortfolioExperienceDataModel {
    id: string|null
    title: string|null
    company: string|null
    location: string|null
    startDate: Date | null
    endDate: Date | null
    description: string|null
    technologies: string[]|null
    highlights: string[]|null
    createdAt?: Date | null
    updatedAt?: Date | null
}

export interface UserPortfolioProjectModel {
    id: string|null
    title: string|null
    imageUrl: string|null
    githubUrl: string|null
    demoUrl: string|null
    description: string|null
    technologies: string[]|null
    dateCreated: Date | null
    userRequest: number|null
    rating: number|null
    releseVersion: string|null
    createdAt?: Date | null
    updatedAt?: Date | null
}