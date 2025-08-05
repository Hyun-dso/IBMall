// /components/ui/SectionTitle.tsx
interface Props {
    title: string;
}

export default function SectionTitle({ title }: Props) {
    return (
        <h2 className="text-xl font-semibold border-b border-border dark:border-dark-border pb-2 mb-4">
            {title}
        </h2>
    );
}